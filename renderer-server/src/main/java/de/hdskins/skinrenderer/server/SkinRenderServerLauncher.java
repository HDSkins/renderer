package de.hdskins.skinrenderer.server;

import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import de.hdskins.skinrenderer.shared.RabbitMQConnector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

public class SkinRenderServerLauncher {

    public static void main(String[] args) throws IOException, TimeoutException {
        Path path = Paths.get("config.yml");
        if (!Files.exists(path)) {
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

        SkinRenderServer server = SkinRenderServer.create(config.getString("rendererName"), config.getString("queue"), config.getInt("priority"), config.get("rabbitMQ", RabbitMQConnector.class), config.getInt("rendererCount"));
        server.start();
    }
}
