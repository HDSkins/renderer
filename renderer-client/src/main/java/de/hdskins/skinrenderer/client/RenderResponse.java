package de.hdskins.skinrenderer.client;

import java.awt.image.BufferedImage;

public class RenderResponse {

    private final boolean success;
    private final String rendererName;
    private final BufferedImage image;
    private final Throwable throwable;

    public RenderResponse(boolean success, String rendererName, BufferedImage image, Throwable throwable) {
        this.success = success;
        this.rendererName = rendererName;
        this.image = image;
        this.throwable = throwable;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getRendererName() {
        return this.rendererName;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }
}
