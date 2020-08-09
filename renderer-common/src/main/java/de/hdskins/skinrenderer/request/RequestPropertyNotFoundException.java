package de.hdskins.skinrenderer.request;

public class RequestPropertyNotFoundException extends RuntimeException {

    private final int id;

    public RequestPropertyNotFoundException(int id) {
        super("RequestProperty with the id " + id + " not found");
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
