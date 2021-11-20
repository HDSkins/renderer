package de.hdskins.skinrenderer.client.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import de.hdskins.skinrenderer.client.RenderResponse;
import de.hdskins.skinrenderer.shared.RabbitMQConsumer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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

        String renderer = null;
        long millis = -1;
        byte[] image = null;
        Throwable throwable = null;

        if (success) {
            try (DataInputStream inputStream = new DataInputStream(byteArrayInputStream)) {
                renderer = inputStream.readUTF();
                millis = inputStream.readLong();
                image = byteArrayInputStream.readAllBytes();
            }
        } else {
            try (ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream)) {
                renderer = inputStream.readUTF();
                millis = inputStream.readLong();
                throwable = (Throwable) inputStream.readObject();
            } catch (ClassNotFoundException exception) {
                throwable = exception;
            }
        }

        future.complete(new RenderResponse(success, renderer, image, throwable, millis));
    }
}
