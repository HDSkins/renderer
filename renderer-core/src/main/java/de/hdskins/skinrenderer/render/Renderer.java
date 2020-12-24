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

package de.hdskins.skinrenderer.render;

import com.google.common.collect.Lists;
import de.hdskins.skinrenderer.ErrorHandling;
import de.hdskins.skinrenderer.RenderContext;
import de.hdskins.skinrenderer.Vertices;
import de.hdskins.skinrenderer.render.primitive.Primitive;
import de.hdskins.skinrenderer.request.RenderRequest;
import org.lwjgl.BufferUtils;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public abstract class Renderer {

    public List<Primitive> primitives = Lists.newArrayList();

    private boolean initialized = false;

    public RenderContext owner;

    private RenderRequest request;
    private boolean back;

    public int skinFbo, skinFboTex;
    public int cubeVbo;

    public Renderer(RenderContext owner) {
        this.owner = owner;
    }

    public void addPrimitive(Primitive primitive) {
        this.primitives.add(primitive);
    }

    protected void preRender(int width, int height) {
    }

    protected void postRender(int width, int height) {
    }

    public void initSkinFbo(int width, int height) {
        this.skinFboTex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.skinFboTex);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        this.skinFbo = glGenFramebuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, this.skinFbo);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.skinFboTex, 0);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        ErrorHandling.checkFramebufferStatus();
    }

    public void render(int width, int height) {
        this.initGL(width, height);
        this.preRender(width, height);
        for (Primitive primitive : this.primitives) {
            primitive.render(this.request, this.back, this);
        }
        this.postRender(width, height);
    }

    public void destroy() {
        this.primitives.clear();
        this.initialized = false;
    }

    public void init(RenderRequest request, boolean back) {
        if (!this.initialized) {
            IntBuffer ids = BufferUtils.createIntBuffer(1);
            glGenBuffers(ids);
            this.cubeVbo = ids.get();

            float[] vertices = back ? Vertices.BACK_VERTICES : Vertices.FRONT_VERTICES;
            FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
            vertexBuffer.put(vertices);
            vertexBuffer.flip();
            glBindBuffer(GL_ARRAY_BUFFER, this.cubeVbo);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        }

        this.request = request;
        this.back = back;
        this.initPrimitives(request, back);
        this.initialized = true;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    protected abstract void initPrimitives(RenderRequest request, boolean back);

    protected void initGL(float width, float height) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glViewport(0, 0, (int) width, (int) height);
        glEnable(GL_DEPTH_TEST);

        double fov = 45;
        double aspect = width / height;

        double zNear = 0.1;
        double zFar = 100;

        double fH = Math.tan((fov / 360) * Math.PI) * zNear;
        double fW = fH * aspect;
        glFrustum(-fW, fW, -fH, fH, zNear, zFar);

        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glEnable(GL_CULL_FACE);
    }

    public void finish() {
        glDeleteTextures(this.skinFboTex);
        glDeleteFramebuffers(this.skinFbo);
    }

    public BufferedImage readPixels(int width, int height) {
        glReadBuffer(GL_FRONT);
        ByteBuffer buf = BufferUtils.createByteBuffer(width * height * 4);
        glReadPixels(0, 0, width, height, GL_BGRA, GL_UNSIGNED_BYTE, buf);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = new int[width * height];
        buf.asIntBuffer().get(pixels);
        img.setRGB(0, 0, width, height, pixels, 0, width);
        return img;
    }
}
