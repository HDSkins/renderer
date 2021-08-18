package de.hdskins.skinrenderer.server;

import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import de.hdskins.skinrenderer.shared.RabbitMQConnector;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SkinRenderServerLauncher {

    public static void main(String[] args) {
        String rendererName = System.getenv("SKIN_RENDERER_NAME");
        String queue = System.getenv("SKIN_RENDERER_QUEUE");
        String rawPriority = System.getenv("SKIN_RENDERER_PRIORITY");
        String rawRendererCount = System.getenv("SKIN_RENDERER_THREAD_COUNT");
        String rabbitMQHost = System.getenv("SKIN_RENDERER_RABBITMQ_HOST");
        String rabbitMQPort = System.getenv("SKIN_RENDERER_RABBITMQ_PORT");
        String rabbitMQUser = System.getenv("SKIN_RENDERER_RABBITMQ_USER");
        String rabbitMQPass = System.getenv("SKIN_RENDERER_RABBITMQ_PASSWORD");

        boolean useConfig = rendererName == null || queue == null || rabbitMQHost == null || rabbitMQUser == null || rabbitMQPass == null;

        Path path = Paths.get("config.yml");
        if (!Files.exists(path) && useConfig) {
            Documents.newDocument()
                    .append("rendererName", "Renderer1")
                    .append("queue", "skinrender")
                    .append("priority", 1)
                    .append("rabbitMQ", new RabbitMQConnector("127.0.0.1", 5672, "", ""))
                    .append("rendererCount", 1)
                    .yaml().write(path);
            System.out.println("config.yml created, please fill in your RabbitMQ credentials");
            return;
        }

        Document config = Documents.yamlStorage().read(path);

        RabbitMQConnector connector;
        int priority = 1;
        int rendererCount = 1;

        if (useConfig) {
            rendererName = config.getString("rendererName");
            queue = config.getString("queue");
            priority = config.getInt("priority");
            connector = config.get("rabbitMQ", RabbitMQConnector.class);
            rendererCount = config.getInt("rendererCount");
        } else {
            try {
                rendererCount = Integer.parseInt(rawRendererCount);
            } catch (NumberFormatException ignored) {
            }
            try {
                priority = Integer.parseInt(rawPriority);
            } catch (NumberFormatException ignored) {
            }

            int port = 5672;
            try {
                port = Integer.parseInt(rabbitMQPort);
            } catch (NumberFormatException ignored) {
            }

            connector = new RabbitMQConnector(rabbitMQHost, port, rabbitMQUser, rabbitMQPass);
        }

        SkinRenderServer server = SkinRenderServer.create(rendererName, queue, priority, connector, rendererCount);
        server.start();
    }
}
