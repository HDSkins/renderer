package de.hdskins.skinrenderer.request;

import de.hdskins.skinrenderer.ImageUtils;
import de.hdskins.skinrenderer.RenderMode;
import de.hdskins.skinrenderer.RenderRotation;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class RenderRequestProperties {

    private static short currentId = 0;

    private static final List<RenderRequestProperty<?>> PROPERTIES_REGISTRY = new ArrayList<>();

    public static final RenderRequestProperty<Integer> WIDTH = simple(RenderMode::getDefaultWidth, DataOutputStream::writeInt, DataInputStream::readInt, RenderMode.values());
    public static final RenderRequestProperty<Integer> HEIGHT = simple(RenderMode::getDefaultHeight, DataOutputStream::writeInt, DataInputStream::readInt, RenderMode.values());
    public static final RenderRequestProperty<BufferedImage> IMAGE = simple((stream, image) -> {
        byte[] bytes = ImageUtils.toByteArray(image);
        stream.writeInt(bytes.length);
        stream.write(bytes);
    }, stream -> {
        byte[] bytes = new byte[stream.readInt()];
        stream.readFully(bytes);
        return ImageUtils.fromByteArray(bytes);
    }, RenderMode.values());

    public static final RenderRequestProperty<RenderRotation> ROTATION = simple(
            mode -> new RenderRotation(mode.getDefaultRotationX(), mode.getDefaultRotationY(), 0),
            (stream, rotation) -> {
                stream.writeInt(rotation.getX());
                stream.writeInt(rotation.getY());
                stream.writeInt(rotation.getLegs());
            },
            stream -> new RenderRotation(stream.readInt(), stream.readInt(), stream.readInt()),
            RenderMode.FULL, RenderMode.BUST, RenderMode.HEAD
    );

    public static final RenderRequestProperty<Boolean> FLIPPED = simple(mode -> false, DataOutputStream::writeBoolean, DataInputStream::readBoolean, RenderMode.values());
    public static final RenderRequestProperty<Boolean> SLIM = simple(mode -> false, DataOutputStream::writeBoolean, DataInputStream::readBoolean, RenderMode.FULL, RenderMode.BUST, RenderMode.FRONT, RenderMode.FRONT_FULL);
    public static final RenderRequestProperty<Boolean> SHADOW = simple(mode -> true, DataOutputStream::writeBoolean, DataInputStream::readBoolean, RenderMode.FULL, RenderMode.BUST, RenderMode.HEAD);

    public static int getNextId() {
        return currentId++;
    }

    @NotNull
    public static <T> RenderRequestProperty<T> simple(ThrowingBiConsumer<DataOutputStream, T, IOException> writer, ThrowingFunction<DataInputStream, T, IOException> reader, RenderMode... applicableModes) {
        return simple(null, writer, reader, applicableModes);
    }

    @NotNull
    public static <T> RenderRequestProperty<T> simple(Function<RenderMode, T> defaultValue, ThrowingBiConsumer<DataOutputStream, T, IOException> writer, ThrowingFunction<DataInputStream, T, IOException> reader, RenderMode... applicableModes) {
        RenderRequestProperty<T> property = new SimpleRenderRequestProperty<>(mode -> Arrays.stream(applicableModes).anyMatch(applicable -> applicable == mode), getNextId(), defaultValue, writer, reader);
        PROPERTIES_REGISTRY.add(property.getId(), property);
        return property;
    }

    public static <T> void writePropertyId(DataOutputStream outputStream, RenderRequestProperty<T> property) throws IOException {
        outputStream.writeShort(property.getId());
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> RenderRequestProperty<T> readPropertyId(DataInputStream inputStream) throws IOException {
        int id = inputStream.readShort();
        RenderRequestProperty<T> property = (RenderRequestProperty<T>) PROPERTIES_REGISTRY.get(id);
        if (property == null) {
            throw new RequestPropertyNotFoundException(id);
        }
        return property;
    }

}
