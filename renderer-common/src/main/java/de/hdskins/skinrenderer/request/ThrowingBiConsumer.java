package de.hdskins.skinrenderer.request;

public interface ThrowingBiConsumer<F, S, E extends Throwable> {

    void accept(F first, S second) throws E;

}
