package de.hdskins.skinrenderer.client;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import de.hdskins.skinrenderer.client.rabbitmq.ClientRabbitMQConsumer;
import de.hdskins.skinrenderer.request.RenderRequest;
import de.hdskins.skinrenderer.shared.RabbitMQConnector;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

public final class SkinRenderClient extends Thread implements AutoCloseable {

    private final Map<String, CompletableFuture<RenderResponse>> pendingJobs = new ConcurrentHashMap<>();

    private final ConnectionFactory factory;
    private Channel channel;

    private final String queue;
    private String replyQueue;

    private boolean running = true;

    private SkinRenderClient(ConnectionFactory factory, String queue) {
        this.factory = factory;
        this.queue = queue;
    }

    public static SkinRenderClient create(String queue, RabbitMQConnector connector) {
        return new SkinRenderClient(connector.createFactory(), queue);
    }

    public CompletableFuture<RenderResponse> render(RenderRequest request) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);

        request.write(outputStream);

        String correlationId = UUID.randomUUID().toString();
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().correlationId(correlationId).replyTo(this.replyQueue).build();

        CompletableFuture<RenderResponse> future = new CompletableFuture<>();

        this.pendingJobs.put(correlationId, future);
        this.channel.basicPublish("", this.queue, properties, byteArrayOutputStream.toByteArray());

        return future;
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
