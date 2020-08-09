/*
 * The MIT License
 *
 * Copyright (c) 2015-2018, Una Thompson (unascribed)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.hdskins.skinrenderer;

import de.hdskins.skinrenderer.render.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class RenderConfiguration {

    private static final Map<RenderMode, Class<? extends Renderer>> RENDERER_REGISTRY = new HashMap<>();

    static {
        registerRenderer(FaceRenderer.class, RenderMode.FACE);
        registerRenderer(FlatBodyRenderer.class, RenderMode.FRONT, RenderMode.FRONT_FULL);
        registerRenderer(BodyRenderer.class, RenderMode.BUST, RenderMode.FULL);
        registerRenderer(HeadRenderer.class, RenderMode.HEAD);
    }

    public static void registerRenderer(Class<? extends Renderer> rendererClass, RenderMode... modes) {
        for (RenderMode mode : modes) {
            if (RENDERER_REGISTRY.containsKey(mode)) {
                throw new IllegalArgumentException("Renderer for mode " + mode + " already registered");
            }
            RENDERER_REGISTRY.put(mode, rendererClass);
        }
    }

    private final CompletableRenderRequest request;

    public RenderConfiguration(CompletableRenderRequest request) {
        this.request = request;
    }

    public Renderer createRenderer(RenderContext owner) {
        Class<? extends Renderer> rendererClass = RENDERER_REGISTRY.get(this.request.getRequest().getMode());
        if (rendererClass == null) {
            throw new IllegalArgumentException("Missing renderer for " + this.request.getRequest().getMode());
        }

        Renderer renderer = null;

        try {
            renderer = rendererClass.getDeclaredConstructor(RenderContext.class).newInstance(owner);
            renderer.init(this.request.getRequest(), this.request.isBack());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            exception.printStackTrace();
        }

        return renderer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        RenderConfiguration that = (RenderConfiguration) o;
        return Objects.equals(this.request, that.request);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.request);
    }
}
