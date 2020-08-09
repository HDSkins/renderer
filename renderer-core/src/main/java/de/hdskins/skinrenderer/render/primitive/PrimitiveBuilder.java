package de.hdskins.skinrenderer.render.primitive;

import de.hdskins.skinrenderer.render.Renderer;
import de.hdskins.skinrenderer.render.TextureType;

import java.util.Arrays;
import java.util.function.Consumer;

public class PrimitiveBuilder<T extends Primitive> {

    private final T primitive;

    public PrimitiveBuilder(Type type) {
        switch (type) {
            case CUBE:
                this.primitive = (T) new Cube();
                break;
            case GROUP:
                this.primitive = (T) new Group();
                break;
            case PLANE:
                this.primitive = (T) new Plane();
                break;
            default:
                throw new IllegalArgumentException("Unknown type " + type);
        }
    }

    public PrimitiveBuilder<T> members(Primitive... primitives) {
        if (!(this.primitive instanceof Group)) {
            throw new IllegalArgumentException("Members are only available for Groups");
        }

        ((Group) this.primitive).members.addAll(Arrays.asList(primitives));
        return this;
    }

    public PrimitiveBuilder<T> x(float x) {
        this.primitive.x = x;
        return this;
    }

    public PrimitiveBuilder<T> y(float y) {
        this.primitive.y = y;
        return this;
    }

    public PrimitiveBuilder<T> z(float z) {
        this.primitive.z = z;
        return this;
    }

    public PrimitiveBuilder<T> rotX(float rotX) {
        this.primitive.rotX = rotX;
        return this;
    }

    public PrimitiveBuilder<T> rotY(float rotY) {
        this.primitive.rotY = rotY;
        return this;
    }

    public PrimitiveBuilder<T> rotZ(float rotZ) {
        this.primitive.rotZ = rotZ;
        return this;
    }

    public PrimitiveBuilder<T> scale(float scale) {
        return this.scale(scale, scale, scale);
    }

    public PrimitiveBuilder<T> scale(float scaleX, float scaleY, float scaleZ) {
        return this.scaleX(scaleX).scaleY(scaleY).scaleZ(scaleZ);
    }

    public PrimitiveBuilder<T> scaleX(float scaleX) {
        this.primitive.scaleX = scaleX;
        return this;
    }

    public PrimitiveBuilder<T> scaleY(float scaleY) {
        this.primitive.scaleY = scaleY;
        return this;
    }

    public PrimitiveBuilder<T> scaleZ(float scaleZ) {
        this.primitive.scaleZ = scaleZ;
        return this;
    }

    public PrimitiveBuilder<T> anchorX(float anchorX) {
        this.primitive.anchorX = anchorX;
        return this;
    }

    public PrimitiveBuilder<T> anchorY(float anchorY) {
        this.primitive.anchorY = anchorY;
        return this;
    }

    public PrimitiveBuilder<T> anchorZ(float anchorZ) {
        this.primitive.anchorZ = anchorZ;
        return this;
    }

    public PrimitiveBuilder<T> lit(boolean lit) {
        this.primitive.lit = lit;
        return this;
    }

    public PrimitiveBuilder<T> textured(boolean textured) {
        this.primitive.textured = textured;
        return this;
    }

    public PrimitiveBuilder<T> execute(Consumer<PrimitiveBuilder<T>> consumer) {
        consumer.accept(this);
        return this;
    }

    public PrimitiveBuilder<T> executeIf(boolean condition, Consumer<PrimitiveBuilder<T>> consumer) {
        if (condition) {
            consumer.accept(this);
        }
        return this;
    }

    public PrimitiveBuilder<T> executeIf(boolean condition, Consumer<PrimitiveBuilder<T>> consumer, Consumer<PrimitiveBuilder<T>> orElseConsumer) {
        if (condition) {
            consumer.accept(this);
        } else {
            orElseConsumer.accept(this);
        }
        return this;
    }

    public PrimitiveBuilder<T> texture(boolean mirror, TextureType texture) {
        TextureType opposite = mirror ? texture.opposite() : null;
        return this.texture(opposite != null ? opposite : texture);
    }

    public PrimitiveBuilder<T> texture(TextureType texture) {
        this.primitive.texture = texture;
        return this;
    }

    public PrimitiveBuilder<T> inState(boolean inStage) {
        this.primitive.inStage = inStage;
        return this;
    }

    public PrimitiveBuilder<T> depthMask(boolean depthMask) {
        this.primitive.depthMask = depthMask;
        return this;
    }

    public T addTo(Group group) {
        group.members.add(this.primitive());
        return this.primitive();
    }

    public T addTo(Renderer renderer) {
        renderer.addPrimitive(this.primitive());
        return this.primitive();
    }

    public T primitive() {
        return this.primitive;
    }

    public static <T extends Primitive> PrimitiveBuilder<T> builder(Type type) {
        return new PrimitiveBuilder<>(type);
    }

    public static PrimitiveBuilder<Group> group() {
        return builder(Type.GROUP);
    }

    public static PrimitiveBuilder<Cube> cube() {
        return builder(Type.CUBE);
    }

    public static PrimitiveBuilder<Plane> plane() {
        return builder(Type.PLANE);
    }

    public enum Type {
        CUBE,
        GROUP,
        PLANE
    }

}
