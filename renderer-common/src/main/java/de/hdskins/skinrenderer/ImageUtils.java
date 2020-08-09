package de.hdskins.skinrenderer;

import com.squareup.gifencoder.GifEncoder;
import com.squareup.gifencoder.ImageOptions;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ImageUtils {

    public static @NotNull BufferedImage scale(@NotNull BufferedImage original, int targetWidth, int targetHeight) {
        var result = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        result.createGraphics().drawImage(original, 0, 0, targetWidth, targetHeight, null);
        return result;
    }

    public static BufferedImage fromByteArray(byte[] bytes) {
        try (var inputStream = new ByteArrayInputStream(bytes)) {
            return ImageIO.read(inputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static byte[] toByteArray(BufferedImage image) {
        try (var outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static @NotNull BufferedImage combineHorizontal(int distance, BufferedImage... images) {
        if (images.length == 0) {
            return new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
        }

        int height = Arrays.stream(images).mapToInt(BufferedImage::getHeight).max().getAsInt();
        for (int i = 0; i < images.length; i++) {
            var image = images[i];

            double ratio = (double) height / (double) image.getHeight();
            images[i] = ImageUtils.scale(image, (int) (image.getWidth() * ratio), (int) (image.getHeight() * ratio));
        }

        int width = Arrays.stream(images).mapToInt(BufferedImage::getWidth).sum() + (distance * (images.length - 1));
        var result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var graphics2D = result.createGraphics();

        int currentX = 0;
        for (BufferedImage image : images) {
            graphics2D.drawImage(image, currentX, 0, null);
            currentX += image.getWidth() + distance;
        }

        return result;
    }

    public static byte[] combineToGifByteArray(int delay, BufferedImage... images) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        combineToGif(outputStream, delay, images);
        return outputStream.toByteArray();
    }

    public static void combineToGif(OutputStream outputStream, long delayMillis, BufferedImage... images) throws IOException {
        if (images.length == 0) {
            return;
        }

        int width = Arrays.stream(images).mapToInt(BufferedImage::getWidth).max().getAsInt();
        int height = Arrays.stream(images).mapToInt(BufferedImage::getHeight).max().getAsInt();

        GifEncoder encoder = new GifEncoder(outputStream, width, height, 0);
        ImageOptions options = new ImageOptions();
        options.setDelay(delayMillis, TimeUnit.MILLISECONDS);

        for (BufferedImage image : images) {

            int[][] rgb = new int[height][width];

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    rgb[y][x] = image.getRGB(x, y);
                }
            }

            encoder.addImage(rgb, options);
        }

        encoder.finishEncoding();
    }

}
