package de.hdskins.skinrenderer.server.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import de.hdskins.skinrenderer.CompletableRenderRequest;
import de.hdskins.skinrenderer.RenderContext;
import de.hdskins.skinrenderer.request.RenderRequest;
import de.hdskins.skinrenderer.server.SkinRenderServer;
import de.hdskins.skinrenderer.shared.RabbitMQConsumer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public class ServerRabbitMQConsumer extends RabbitMQConsumer {

    private final SkinRenderServer server;

    public ServerRabbitMQConsumer(Channel channel, SkinRenderServer server) {
        super(channel);
        this.server = server;
    }

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        try {
            this.server.reconnect();
        } catch (IOException | TimeoutException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void handleDelivery0(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        RenderContext context = this.server.nextRenderer();
        CompletableRenderRequest request = this.createRequest(envelope, properties, body);

        context.queueRequest(request);
    }

    private CompletableRenderRequest createRequest(Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(body));

        RenderRequest request = RenderRequest.read(inputStream);

        CompletableFuture<BufferedImage> future = new CompletableFuture<>();

        future.thenAccept(response -> {
            try {
                this.sendResponse(envelope, properties, this.createResponse(response));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            try {
                this.sendResponse(envelope, properties, this.createResponse(throwable));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return null;
        });

        return new CompletableRenderRequest(request, future);
    }

    private void sendResponse(Envelope envelope, AMQP.BasicProperties properties, byte[] response) {
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder().correlationId(properties.getCorrelationId()).build();

        try {
            this.getChannel().basicPublish("", properties.getReplyTo(), basicProperties, response);
            this.getChannel().basicAck(envelope.getDeliveryTag(), false);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private byte[] createResponse(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(1);

        try (DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream)) {
            outputStream.writeUTF(this.server.getWorkerName());

            ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", imageStream);
            outputStream.write(imageStream.toByteArray());
        }

        return byteArrayOutputStream.toByteArray();
    }

    private byte[] createResponse(Throwable throwable) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(0);

        try (ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            outputStream.writeUTF(this.server.getWorkerName());
            outputStream.writeObject(throwable);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
