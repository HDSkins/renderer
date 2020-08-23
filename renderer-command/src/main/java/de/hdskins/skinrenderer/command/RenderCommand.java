package de.hdskins.skinrenderer.command;

import com.github.derrop.simplecommand.CommandProperties;
import com.github.derrop.simplecommand.annotation.Argument;
import com.github.derrop.simplecommand.annotation.Command;
import com.github.derrop.simplecommand.annotation.SubCommand;
import com.github.derrop.simplecommand.argument.ArgumentType;
import com.github.derrop.simplecommand.argument.CommandArgumentWrapper;
import com.github.derrop.simplecommand.argument.DefaultArgumentTypes;
import com.github.derrop.simplecommand.sender.CommandSender;
import de.hdskins.skinrenderer.CompletableRenderRequest;
import de.hdskins.skinrenderer.RenderMode;
import de.hdskins.skinrenderer.RenderRotation;
import de.hdskins.skinrenderer.request.RenderRequest;
import de.hdskins.skinrenderer.request.RenderRequestProperties;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Command(aliases = "render", description = "Allows the rendering of skins")
public class RenderCommand {

    private final SkinRenderCommand command;

    public RenderCommand(SkinRenderCommand command) {
        this.command = command;
    }

    @Argument
    public final ArgumentType<RenderMode> renderMode = DefaultArgumentTypes.exactEnum(RenderMode.class);

    @Argument
    public final ArgumentType<String> path = DefaultArgumentTypes.dynamicString(
            "path",
            "The given path doesn't exist or is a directory",
            path -> Files.exists(Paths.get(path)) && !Files.isDirectory(Paths.get(path))
    );

    @SubCommand(args = {"renderMode", "path"}, enableProperties = true, extendedUsage = " | --flipped | --noShadow | --slim | rotX=<rotationX> | rotY=<rotationY> | legs=<rotationLegs>")
    public void render(CommandSender sender, CommandArgumentWrapper args, CommandProperties properties) {
        RenderMode mode = (RenderMode) args.argument(0);
        Path path = Paths.get((String) args.argument("path"));

        BufferedImage image;
        try (InputStream inputStream = Files.newInputStream(path)) {
            image = ImageIO.read(inputStream);
        } catch (IOException exception) {
            sender.sendMessage("Invalid image: " + exception.getMessage());
            return;
        }

        if (image.getHeight() != image.getWidth() && image.getHeight() != image.getWidth() / 2) {
            sender.sendMessage("The height and width of the image have to be either the same or the height has to be the width / 2");
            return;
        }

        sender.sendMessage("Rendering the skin at " + path.toAbsolutePath() + "...");

        RenderRequest.Builder builder = RenderRequest.builder(mode).withProperty(RenderRequestProperties.IMAGE, image);

        if (properties.getString("flipped") != null) {
            builder.withProperty(RenderRequestProperties.FLIPPED, properties.getBoolean("flipped"));
        }
        if (properties.getBoolean("noShadow")) {
            builder.withProperty(RenderRequestProperties.SHADOW, false, true);
        }
        int rotationX = properties.getInt("rotX", mode.getDefaultRotationX());
        int rotationY = properties.getInt("rotY", mode.getDefaultRotationY());
        int rotationLegs = properties.getInt("legs", 0);
        builder.withProperty(RenderRequestProperties.ROTATION, new RenderRotation(rotationX, rotationY, rotationLegs), true);

        if (properties.getBoolean("slim")) {
            builder.withProperty(RenderRequestProperties.SLIM, true);
        }

        CompletableFuture<BufferedImage> future = new CompletableFuture<>();

        long begin = System.currentTimeMillis();

        this.command.getRenderContext().queueRequest(new CompletableRenderRequest(builder.build(), future));

        future.thenAccept(result -> {
            long time = System.currentTimeMillis() - begin;
            double seconds = (double) time / 1000D;

            Path outputPath = Paths.get(UUID.randomUUID().toString() + ".png");

            sender.sendMessage(String.format("Successfully rendered %s (took %.3f seconds), saving the result to %s...", path.toAbsolutePath(), seconds, outputPath));
            try (OutputStream outputStream = Files.newOutputStream(outputPath)) {
                ImageIO.write(result, "PNG", outputStream);

                sender.sendMessage("Successfully saved the image to " + outputPath);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

    }

}
