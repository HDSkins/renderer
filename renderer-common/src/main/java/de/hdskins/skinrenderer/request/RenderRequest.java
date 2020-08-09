package de.hdskins.skinrenderer.request;

import de.hdskins.skinrenderer.RenderMode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RenderRequest {

    private final RenderMode mode;
    private final Map<RenderRequestProperty<Object>, Object> properties;

    public RenderRequest(RenderMode mode, Map<RenderRequestProperty<Object>, Object> properties) {
        this.mode = mode;
        this.properties = properties;
    }

    public RenderMode getMode() {
        return this.mode;
    }

    public boolean isFull() {
        return this.mode.isTall() && this.mode != RenderMode.HEAD && this.mode != RenderMode.FACE;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(RenderRequestProperty<T> property) {
        return (T) this.properties.getOrDefault(property, property.getDefaultValue(this.mode));
    }

    public static RenderRequest read(DataInputStream inputStream) throws IOException {
        RenderMode mode = RenderMode.values()[inputStream.readByte()];
        int size = inputStream.readByte();
        Map<RenderRequestProperty<Object>, Object> properties = new HashMap<>();

        for (int i = 0; i < size; i++) {
            RenderRequestProperty<Object> property = RenderRequestProperties.readPropertyId(inputStream);
            properties.put(property, property.readValue(inputStream));
        }

        return new RenderRequest(mode, properties);
    }

    public void write(DataOutputStream outputStream) throws IOException {
        outputStream.writeByte(this.mode.ordinal());
        outputStream.writeByte(this.properties.size());

        for (Map.Entry<RenderRequestProperty<Object>, Object> entry : this.properties.entrySet()) {
            RenderRequestProperties.writePropertyId(outputStream, entry.getKey());
            this.writeProperty(outputStream, entry.getKey(), entry.getValue());
        }

    }

    private void writeProperty(DataOutputStream outputStream, RenderRequestProperty<Object> property, Object value) throws IOException {
        property.writeValue(outputStream, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderRequest that = (RenderRequest) o;
        return mode == that.mode &&
                Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode, properties);
    }

    public static Builder builder(RenderMode mode) {
        return new Builder(mode);
    }

    public static final class Builder {

        private final RenderMode mode;
        private final Map<RenderRequestProperty<Object>, Object> properties = new HashMap<>();

        private Builder(RenderMode mode) {
            this.mode = mode;
        }

        public <T> Builder withProperty(RenderRequestProperty<T> property, T value) {
            return this.withProperty(property, value, false);
        }

        @SuppressWarnings("unchecked")
        public <T> Builder withProperty(RenderRequestProperty<T> property, T value, boolean suppressNotApplicable) {
            if (this.properties.containsKey(property)) {
                throw new IllegalStateException("Property " + property.getId() + " already set");
            }
            if (!property.isApplicableForMode(this.mode)) {
                if (suppressNotApplicable) {
                    return this;
                }
                throw new IllegalStateException("Property " + property.getId() + " is not applicable for the mode " + this.mode);
            }
            this.properties.put((RenderRequestProperty<Object>) property, value);
            return this;
        }

        public RenderRequest build() {
            return new RenderRequest(this.mode, this.properties);
        }

    }

}
