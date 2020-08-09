package de.hdskins.skinrenderer.shared;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class RabbitMQConsumer extends DefaultConsumer {

    public RabbitMQConsumer(Channel channel) {
        super(channel);
    }

    private final AtomicBoolean closed = new AtomicBoolean();

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        this.closed.set(true);
    }

    @Override
    public void handleCancel(String consumerTag) {
        this.closed.set(true);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        if (!this.closed.get()) {
            this.handleDelivery0(consumerTag, envelope, properties, body);
        }
    }

    public abstract void handleDelivery0(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException;
}
