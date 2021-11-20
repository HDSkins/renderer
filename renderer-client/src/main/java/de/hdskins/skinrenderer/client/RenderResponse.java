package de.hdskins.skinrenderer.client;

public class RenderResponse {

    private final boolean success;
    private final String rendererName;
    private final byte[] image;
    private final Throwable throwable;
    private final long millis;

    public RenderResponse(boolean success, String rendererName, byte[] image, Throwable throwable, long millis) {
        this.success = success;
        this.rendererName = rendererName;
        this.image = image;
        this.throwable = throwable;
        this.millis = millis;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getRendererName() {
        return this.rendererName;
    }

    public byte[] getImage() {
        return this.image;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public long getMillis() {
        return this.millis;
    }
}
