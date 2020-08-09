package de.hdskins.skinrenderer.request;

import de.hdskins.skinrenderer.RenderMode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class RenderRequestProperty<T> {

    private final int id;
    private final Function<RenderMode, T> defaultValue;
    private final Predicate<RenderMode> modeTester;

    public RenderRequestProperty(int id, Function<RenderMode, T> defaultValue) {
        this(id, defaultValue, renderMode -> true);
    }

    public RenderRequestProperty(int id, Function<RenderMode, T> defaultValue, Predicate<RenderMode> modeTester) {
        this.id = id;
        this.defaultValue = defaultValue;
        this.modeTester = modeTester;
    }

    public int getId() {
        return this.id;
    }

    public T getDefaultValue(RenderMode mode) {
        return this.defaultValue == null ? null : this.defaultValue.apply(mode);
    }

    public boolean isApplicableForMode(RenderMode mode) {
        return this.modeTester.test(mode);
    }

    public abstract void writeValue(DataOutputStream outputStream, T value) throws IOException;

    public abstract T readValue(DataInputStream inputStream) throws IOException;

}
