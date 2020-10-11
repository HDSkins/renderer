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

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import de.hdskins.skinrenderer.render.Renderer;
import de.hdskins.skinrenderer.request.RenderRequestProperties;
import de.hdskins.skinrenderer.util.Textures;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class RenderContext extends Thread implements AutoCloseable {

    private static final int CANVAS_WIDTH = 512;
    private static final int CANVAS_HEIGHT = 832;

    private static final BufferedImage shadow;
    private static final BufferedImage skinUnderlay;

    static {
        try {
            shadow = ImageIO.read(ClassLoader.getSystemResource("shadow.png"));
            skinUnderlay = ImageIO.read(ClassLoader.getSystemResource("skin_underlay.png"));

            shadow.coerceData(true);
            skinUnderlay.coerceData(true);
        } catch (IOException e) {
            throw new InternalError(e);
        }
    }

    private static int nextId = 1;

    public int planeVbo, skinTexture, shadowTexture, skinUnderlayTexture;

    public int fbo, swapFbo, swapFboTex;

    public int textureFilterProgram;

    private boolean run = true;

    private final BlockingDeque<CompletableRenderRequest> requestQueue = new LinkedBlockingDeque<>();

    private final boolean requiresInitialization;
    private long window;

    public RenderContext(boolean requiresInitialization) {
        super("Render thread #" + (nextId++));
        this.requiresInitialization = requiresInitialization;
    }

    @Override
    public void run() {
        try {
            if (this.requiresInitialization) {
                this.init();
            }

            try {

                while (this.run) {

                    try {
                        this.processRequestSafe(this.requestQueue.take());
                    } catch (InterruptedException interrupted) {
                        break;
                    }

                }

                if (this.requiresInitialization) {
                    glfwDestroyWindow(this.window);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws IOException {
        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW");
        }

        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_FALSE);

        this.window = glfwCreateWindow(CANVAS_WIDTH, CANVAS_HEIGHT, "Visage v" + Visage.VERSION + " [" + this.getName() + "]", NULL, NULL);
        if (this.window == NULL) {
            throw new RuntimeException("Failed to create window");
        }
        glfwMakeContextCurrent(this.window);
        GL.createCapabilities();

        if (!GL.getCapabilities().OpenGL30) {
            throw new RuntimeException("OpenGL 3.0 is required");
        }
        if (!GL.getCapabilities().GL_ARB_texture_multisample) {
            throw new RuntimeException("ARB_texture_multisample is required");
        }

        IntBuffer ids = BufferUtils.createIntBuffer(1);
        glGenBuffers(ids);
        this.planeVbo = ids.get();

        IntBuffer textures = BufferUtils.createIntBuffer(4);
        glGenTextures(textures);
        this.skinTexture = textures.get();
        this.shadowTexture = textures.get();
        this.skinUnderlayTexture = textures.get();
        this.swapFboTex = textures.get();

        glBindTexture(GL_TEXTURE_2D, this.skinTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        Textures.upload(shadow, GL_RGBA8, this.shadowTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        Textures.upload(skinUnderlay, GL_RGBA8, this.skinUnderlayTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glBindTexture(GL_TEXTURE_2D, this.swapFboTex);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, CANVAS_WIDTH, CANVAS_HEIGHT, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        this.fbo = glGenFramebuffers();

        int depth = glGenRenderbuffers();
        int color = glGenRenderbuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);

        glBindRenderbuffer(GL_RENDERBUFFER, depth);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, 8, GL_DEPTH_COMPONENT24, CANVAS_WIDTH, CANVAS_HEIGHT);

        glBindRenderbuffer(GL_RENDERBUFFER, color);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, 8, GL_RGBA8, CANVAS_WIDTH, CANVAS_HEIGHT);

        glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depth);
        glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, color);

        this.swapFbo = glGenFramebuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, this.swapFbo);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.swapFboTex, 0);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        this.textureFilterProgram = glCreateProgram();

        int textureFilterVS = glCreateShader(GL_VERTEX_SHADER);
        int textureFilterFS = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(textureFilterVS, Resources.toString(ClassLoader.getSystemResource("texturefilter.vs"), Charsets.UTF_8));
        glShaderSource(textureFilterFS, Resources.toString(ClassLoader.getSystemResource("texturefilter.fs"), Charsets.UTF_8));

        glCompileShader(textureFilterVS);
        glCompileShader(textureFilterFS);

        glAttachShader(this.textureFilterProgram, textureFilterVS);
        glAttachShader(this.textureFilterProgram, textureFilterFS);

        glLinkProgram(this.textureFilterProgram);

        FloatBuffer planeVertexBuffer = BufferUtils.createFloatBuffer(Vertices.PLANE_VERTICES.length);
        planeVertexBuffer.put(Vertices.PLANE_VERTICES);
        planeVertexBuffer.flip();
        glBindBuffer(GL_ARRAY_BUFFER, this.planeVbo);
        glBufferData(GL_ARRAY_BUFFER, planeVertexBuffer, GL_STATIC_DRAW);

        glClearColor(0, 0, 0, 0);
        glClearDepth(1.0);

        glShadeModel(GL_SMOOTH);
        glCullFace(GL_BACK);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        FloatBuffer lightColor = BufferUtils.createFloatBuffer(4);
        lightColor.put(3f);
        lightColor.put(3f);
        lightColor.put(3f);
        lightColor.put(1.0f);
        lightColor.flip();
        glLightfv(GL_LIGHT0, GL_AMBIENT, lightColor);

        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(-4f);
        lightPosition.put(-2f);
        lightPosition.put(1f);
        lightPosition.put(1000f);
        lightPosition.flip();
        glLightfv(GL_LIGHT0, GL_POSITION, lightPosition);

        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_RESCALE_NORMAL);
        glFrontFace(GL_CW);
        glShadeModel(GL_SMOOTH);
    }

    public void queueRequest(CompletableRenderRequest request) {
        this.requestQueue.add(request);
    }

    private void processRequestSafe(CompletableRenderRequest request) {
        try {
            this.processRequest(request);
        } catch (Exception e) {
            if (!request.getFuture().isDone()) {
                request.getFuture().completeExceptionally(e);
            }
        }
    }

    private void processRequest(CompletableRenderRequest request) {
        RenderConfiguration config = new RenderConfiguration(request);

        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        BufferedImage output = this.draw(config,
                request.getRequest().getProperty(RenderRequestProperties.WIDTH),
                request.getRequest().getProperty(RenderRequestProperties.HEIGHT),
                request.getRequest().getProperty(RenderRequestProperties.IMAGE)
        );

        request.getFuture().complete(output);
    }

    public BufferedImage draw(RenderConfiguration conf, int width, int height, BufferedImage image) {
        float factor = (float) (image.getWidth() / 64D);

        if (image.getHeight() == image.getWidth() / 2) { // legacy skin
            image = LegacyFormatter.asNonLegacy(image);
        }

        BufferedImage out;
        int color = image.getRGB(32, 8);
        boolean equal = true;
        for (int x = (int) (32 * factor); x < 64 * factor; x++) {
            for (int y = 0; y < 16 * factor; y++) {
                if (x < 40 && y < 8) continue;
                if (x > 54 && y < 8) continue;
                if (image.getRGB(x, y) != color) {
                    equal = false;
                    break;
                }
            }
        }
        if (equal) {
            image.setRGB((int) (32 * factor), 0, (int) (32 * factor), (int) (16 * factor), new int[(image.getHeight() / 2) * image.getWidth()], 0, (int) (32 * factor));
        }

        Renderer renderer = conf.createRenderer(this);

        renderer.initSkinFbo(image.getWidth(), image.getHeight());

        try {
            Textures.upload(image, GL_RGBA8, this.skinTexture);

            glUseProgram(0);

            glBindFramebuffer(GL_FRAMEBUFFER, renderer.skinFbo);
            glClearColor(0, 0, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glViewport(0, 0, (int) (64 * factor), (int) (64 * factor));
            glOrtho(0, (int) (64 * factor), 0, (int) (64 * factor), -1, 1);

            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();

            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

            glEnable(GL_TEXTURE_2D);

            glDisable(GL_LIGHTING);
            glColor3f(1, 1, 1);
            glDisable(GL_ALPHA_TEST);
            glDisable(GL_CULL_FACE);

            glBindTexture(GL_TEXTURE_2D, this.skinUnderlayTexture);
            this.drawQuad(0, 0, image.getWidth(), image.getWidth());

            glBindTexture(GL_TEXTURE_2D, this.skinTexture);
            drawQuad(0, 0, 64 * factor, 64 * factor);


            glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);

            glClearColor(0, 0, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            renderer.render(width, height);

            glUseProgram(0);

            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, this.swapFbo);
            glBindFramebuffer(GL_READ_FRAMEBUFFER, this.fbo);
            glBlitFramebuffer(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT, GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST);

            glBindFramebuffer(GL_FRAMEBUFFER, 0);

            glDisable(GL_LIGHTING);
            glColor3f(1, 1, 1);
            glDisable(GL_ALPHA_TEST);
            glDisable(GL_CULL_FACE);
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(0, 1, 0, 1, -10, 10);
            glViewport(0, 0, 1, 1);

            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();

            glViewport(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
            glBindTexture(GL_TEXTURE_2D, this.swapFboTex);
            this.drawQuad(0, 0, 1, 1);

            out = renderer.readPixels(width, height);
        } finally {
            renderer.finish();
            renderer.destroy();
        }

        return out;
    }

    private void drawQuad(float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2) {
        glBegin(GL_QUADS);

        glTexCoord2f(u1, v1);
        glVertex2f(x1, y1);
        glTexCoord2f(u2, v1);
        glVertex2f(x2, y1);
        glTexCoord2f(u2, v2);
        glVertex2f(x2, y2);
        glTexCoord2f(u1, v2);
        glVertex2f(x1, y2);

        glEnd();
    }

    private void drawQuad(float x1, float y1, float x2, float y2) {
        this.drawQuad(x1, y1, x2, y2, 0, 0, 1, 1);
    }

    public void close() {
        this.run = false;
        this.interrupt();
    }

}

