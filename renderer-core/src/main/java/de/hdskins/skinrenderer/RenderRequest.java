package de.hdskins.skinrenderer;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class RenderRequest {

    private final RenderMode mode;
    private final int width;
    private final int height;
    private final BufferedImage image;
    private final int rotationX;
    private final int rotationY;
    private final int legRotation;
    private final boolean back;
    private final boolean flipped;
    private final boolean slim;
    private final CompletableFuture<BufferedImage> future;

    public RenderRequest(RenderMode mode, int width, int height, BufferedImage image, int rotationX, int rotationY, int legRotation, boolean flipped, boolean slim, CompletableFuture<BufferedImage> future) {
        this.mode = mode;
        this.width = width;
        this.height = height;
        this.image = image;
        this.rotationY = rotationY;
        this.legRotation = legRotation;
        rotationX %= 360;
        if (rotationX < 0) {
            rotationX = 360 + rotationX;
        }
        this.back = rotationX > 90 && rotationX < 270;
        this.rotationX = this.back ? rotationX - 180 : rotationX;
        this.flipped = flipped;
        this.slim = slim;
        this.future = future;
    }

    public static RenderRequest createDefault(RenderMode mode, BufferedImage image, boolean flipped, boolean slim) {
        return createDefault(mode, image, mode.getDefaultRotationX(), mode.getDefaultRotationY(), 0, flipped, slim);
    }

    public static RenderRequest createDefault(RenderMode mode, BufferedImage image, boolean flipped, boolean slim, CompletableFuture<BufferedImage> future) {
        return createDefault(mode, image, mode.getDefaultRotationX(), mode.getDefaultRotationY(), 0, flipped, slim, future);
    }

    public static RenderRequest createDefault(RenderMode mode, BufferedImage image, int rotationX, int rotationY, int legRotation, boolean flipped, boolean slim) {
        return createDefault(mode, image, rotationX, rotationY, legRotation, flipped, slim, new CompletableFuture<>());
    }

    public static RenderRequest createDefault(RenderMode mode, BufferedImage image, int rotationX, int rotationY, int legRotation, boolean flipped, boolean slim, CompletableFuture<BufferedImage> future) {
        return new RenderRequest(mode, mode.getDefaultWidth(), mode.getDefaultHeight(), image, rotationX, rotationY, legRotation, flipped, slim, future);
    }

    public RenderMode getMode() {
        return this.mode;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public int getRotationX() {
        return this.rotationX;
    }

    public int getRotationY() {
        return this.rotationY;
    }

    public int getLegRotation() {
        return this.legRotation;
    }

    public boolean isFlipped() {
        return this.flipped;
    }

    public boolean isSlim() {
        return this.slim && this.mode != RenderMode.HEAD && this.mode != RenderMode.FACE;
    }

    public boolean isBack() {
        return this.back;
    }

    public CompletableFuture<BufferedImage> getFuture() {
        return this.future;
    }

    public boolean isFull() {
        return this.mode.isTall() && this.mode != RenderMode.HEAD && this.mode != RenderMode.FACE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        RenderRequest that = (RenderRequest) o;
        return this.width == that.width &&
                this.height == that.height &&
                this.rotationX == that.rotationX &&
                this.rotationY == that.rotationY &&
                this.legRotation == that.legRotation &&
                this.back == that.back &&
                this.flipped == that.flipped &&
                this.slim == that.slim &&
                this.mode == that.mode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.mode, this.width, this.height, this.rotationX, this.rotationY, this.legRotation, this.back, this.flipped, this.slim);
    }
}
