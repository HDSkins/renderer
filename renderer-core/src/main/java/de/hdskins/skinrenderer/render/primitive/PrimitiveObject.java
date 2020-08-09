package de.hdskins.skinrenderer.render.primitive;
import de.hdskins.skinrenderer.render.Renderer;
import de.hdskins.skinrenderer.request.RenderRequest;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;

public abstract class PrimitiveObject extends Primitive {

    private int tcbo = Integer.MAX_VALUE;

    protected abstract int vbo(Renderer renderer);

    protected abstract float[] vertices(RenderRequest request, boolean back, Renderer renderer);

    @Override
    public void render(RenderRequest request, boolean back, Renderer renderer) {
        if (this.tcbo == Integer.MAX_VALUE) {
            this.tcbo = glGenBuffers();
            FloatBuffer uv = BufferUtils.createFloatBuffer(this.texture.u.length + this.texture.v.length);
            for (int i = 0; i < this.texture.u.length; i++) {
                uv.put(this.texture.u[i]);
                uv.put(this.texture.v[i]);
            }
            uv.flip();
            glBindBuffer(GL_ARRAY_BUFFER, this.tcbo);
            glBufferData(GL_ARRAY_BUFFER, uv, GL_STATIC_DRAW);
        }
        this.doRender(renderer, this.vbo(renderer), this.tcbo, this.vertices(request, back, renderer));
    }
}
