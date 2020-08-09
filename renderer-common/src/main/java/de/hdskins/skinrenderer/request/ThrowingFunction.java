package de.hdskins.skinrenderer.request;

public interface ThrowingFunction<V, R, E extends Throwable> {

    R accept(V value) throws E;

}
