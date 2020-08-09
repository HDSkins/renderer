package de.hdskins.skinrenderer.client;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import de.hdskins.skinrenderer.client.rabbitmq.ClientRabbitMQConsumer;
import de.hdskins.skinrenderer.shared.RabbitMQConnector;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

public class SkinRenderClient extends Thread implements AutoCloseable {

    private final Map<String, CompletableFuture<RenderResponse>> pendingJobs = new ConcurrentHashMap<>();

    private final ConnectionFactory factory;
    private Channel channel;

    private final String queue;
    private String replyQueue;

    private boolean running = true;

    public SkinRenderClient(ConnectionFactory factory, String queue) {
        this.factory = factory;
        this.queue = queue;
    }

    public static SkinRenderClient create(String queue, RabbitMQConnector connector) {
        return new SkinRenderClient(connector.createFactory(), queue);
    }

    public void render(RenderRequest request) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);

        outputStream.writeByte(request.getMode().ordinal());
        outputStream.writeInt(request.getWidth());
        outputStream.writeInt(request.getHeight());

        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        ImageIO.write(request.getImage(), "PNG", imageStream);
        outputStream.writeInt(imageStream.size());
        outputStream.write(imageStream.toByteArray());

        outputStream.writeInt(request.getRotationX());
        outputStream.writeInt(request.getRotationY());
        outputStream.writeInt(request.getLegRotation());
        outputStream.writeBoolean(request.isFlipped());
        outputStream.writeBoolean(request.isSlim());

        String correlationId = UUID.randomUUID().toString();
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().correlationId(correlationId).replyTo(this.replyQueue).build();

        this.pendingJobs.put(correlationId, request.getFuture());
        this.channel.basicPublish("", this.queue, properties, byteArrayOutputStream.toByteArray());
    }

    @Override
    public void run() {
        try {
            Connection connection = this.factory.newConnection();
            this.channel = connection.createChannel();

            this.channel.queueDeclare(this.queue, false, false, true, null);
            this.channel.basicQos(1);

            this.replyQueue = this.channel.queueDeclare().getQueue();
            this.channel.basicConsume(this.replyQueue, true, new ClientRabbitMQConsumer(this.channel, this.pendingJobs));

            while (this.running) {
                Thread.sleep(Long.MAX_VALUE); // TODO: better solution than that?
            }
        } catch (IOException | TimeoutException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void close() {
        this.running = false;
        super.interrupt();
    }
}
