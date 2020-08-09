package de.hdskins.skinrenderer.client.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import de.hdskins.skinrenderer.client.RenderResponse;
import de.hdskins.skinrenderer.shared.RabbitMQConsumer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ClientRabbitMQConsumer extends RabbitMQConsumer {

    private final Map<String, CompletableFuture<RenderResponse>> pendingJobs;

    public ClientRabbitMQConsumer(Channel channel, Map<String, CompletableFuture<RenderResponse>> pendingJobs) {
        super(channel);
        this.pendingJobs = pendingJobs;
    }

    @Override
    public void handleDelivery0(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        CompletableFuture<RenderResponse> future = this.pendingJobs.remove(properties.getCorrelationId());
        if (future == null) {
            return;
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        boolean success = byteArrayInputStream.read() == 1;
        RenderResponse response;

        if (success) {
            try (DataInputStream inputStream = new DataInputStream(byteArrayInputStream)) {
                String renderer = inputStream.readUTF();
                BufferedImage image = ImageIO.read(byteArrayInputStream);

                response = new RenderResponse(true, renderer, image, null);
            }
        } else {
            String renderer = null;
            Throwable throwable;
            try (ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream)) {
                renderer = inputStream.readUTF();
                throwable = (Throwable) inputStream.readObject();
            } catch (ClassNotFoundException exception) {
                throwable = exception;
            }
            response = new RenderResponse(false, renderer, null, throwable);
        }

        future.complete(response);
    }
}
