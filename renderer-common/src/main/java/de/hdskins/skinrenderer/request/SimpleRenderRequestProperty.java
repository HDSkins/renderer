package de.hdskins.skinrenderer.request;

import de.hdskins.skinrenderer.RenderMode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;

public class SimpleRenderRequestProperty<T> extends RenderRequestProperty<T> {

    private final ThrowingBiConsumer<DataOutputStream, T, IOException> writer;
    private final ThrowingFunction<DataInputStream, T, IOException> reader;

    public SimpleRenderRequestProperty(Predicate<RenderMode> modeTester, int id, Function<RenderMode, T> defaultValue, ThrowingBiConsumer<DataOutputStream, T, IOException> writer, ThrowingFunction<DataInputStream, T, IOException> reader) {
        super(id, defaultValue, modeTester);
        this.writer = writer;
        this.reader = reader;
    }

    @Override
    public void writeValue(DataOutputStream outputStream, T value) throws IOException {
        this.writer.accept(outputStream, value);
    }

    @Override
    public T readValue(DataInputStream inputStream) throws IOException {
        return this.reader.accept(inputStream);
    }
}
