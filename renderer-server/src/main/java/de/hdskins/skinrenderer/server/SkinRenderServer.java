package de.hdskins.skinrenderer.server;

import com.google.common.base.Preconditions;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import de.hdskins.skinrenderer.RenderContext;
import de.hdskins.skinrenderer.server.rabbitmq.ServerRabbitMQConsumer;
import de.hdskins.skinrenderer.shared.RabbitMQConnector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class SkinRenderServer extends Thread implements AutoCloseable {

    private final String name;
    private final int priority;

    private int currentRendererId = 0;
    private final List<RenderContext> renderers = new ArrayList<>();

    private final ConnectionFactory factory;
    private final String queue;
    private Channel channel;

    private boolean running = true;

    public RenderContext nextRenderer() {
        return this.renderers.size() >= this.currentRendererId ? this.renderers.get(this.currentRendererId = 0) : this.renderers.get(this.currentRendererId++);
    }

    public SkinRenderServer(String name, int priority, ConnectionFactory factory, String queue) {
        this.name = name;
        this.priority = priority;
        this.factory = factory;
        this.queue = queue;
    }

    public static SkinRenderServer create(String name, String queue, int priority, RabbitMQConnector connector, int renderers) throws IOException, TimeoutException {
        Preconditions.checkArgument(renderers > 0, "Must be at least 1 renderer");

        SkinRenderServer server = new SkinRenderServer(name, priority, connector.createFactory(), queue);

        for (int i = 0; i < renderers; i++) {
            RenderContext context = new RenderContext(i == 0);
            server.renderers.add(context);
        }

        return server;
    }

    public void reconnect() throws IOException, TimeoutException {
        Connection connection = this.factory.newConnection();
        this.channel = connection.createChannel();

        this.channel.queueDeclare(this.queue, false, false, true, null);
    }

    @Override
    public void run() {
        for (RenderContext renderer : this.renderers) {
            renderer.start();
        }

        try {
            this.reconnect();

            this.channel.basicConsume(this.queue, false, Map.of("x-priority", this.priority), new ServerRabbitMQConsumer(this.channel, this));
            while (this.running) {
                Thread.sleep(Long.MAX_VALUE);
            }
        } catch (IOException | InterruptedException | TimeoutException exception) {
            exception.printStackTrace();
        }
    }

    public String getWorkerName() {
        return this.name;
    }

    @Override
    public void close() {
        this.running = false;
        super.interrupt();
    }
}
