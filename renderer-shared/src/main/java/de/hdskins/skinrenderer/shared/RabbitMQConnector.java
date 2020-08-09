package de.hdskins.skinrenderer.shared;

import com.rabbitmq.client.ConnectionFactory;
import org.jetbrains.annotations.NotNull;

public class RabbitMQConnector {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public RabbitMQConnector(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public @NotNull ConnectionFactory createFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(this.host);
        factory.setPort(this.port);
        if (!this.username.isEmpty() && !this.password.isEmpty()) {
            factory.setUsername(this.username);
            factory.setPassword(this.password);
        }

        factory.setRequestedHeartbeat(10);
        return factory;
    }
}
