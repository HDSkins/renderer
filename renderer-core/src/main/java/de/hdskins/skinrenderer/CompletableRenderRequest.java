package de.hdskins.skinrenderer;

import de.hdskins.skinrenderer.request.RenderRequest;
import de.hdskins.skinrenderer.request.RenderRequestProperties;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

public class CompletableRenderRequest {

    private final RenderRequest request;
    private final boolean back;
    private final CompletableFuture<BufferedImage> future;

    public CompletableRenderRequest(RenderRequest request, CompletableFuture<BufferedImage> future) {
        this.request = request;
        this.future = future;

        int rotationX = request.getProperty(RenderRequestProperties.ROTATION).getX();
        rotationX %= 360;
        if (rotationX < 0) {
            rotationX = 360 + rotationX;
        }
        this.back = rotationX > 90 && rotationX < 270;
        request.getProperty(RenderRequestProperties.ROTATION).setX(this.back ? rotationX - 180 : rotationX);
    }

    public RenderRequest getRequest() {
        return this.request;
    }

    public boolean isBack() {
        return this.back;
    }

    public CompletableFuture<BufferedImage> getFuture() {
        return this.future;
    }
}
