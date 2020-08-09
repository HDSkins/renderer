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

package de.hdskins.skinrenderer.render.primitive;

import de.hdskins.skinrenderer.RenderRequest;
import de.hdskins.skinrenderer.render.Renderer;
import de.hdskins.skinrenderer.render.TextureType;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glUseProgram;

public abstract class Primitive {

    public float scaleX = 1.0f;
    public float scaleY = 1.0f;
    public float scaleZ = 1.0f;
    public float x, y, z, rotX, rotY, rotZ;
    public float anchorX, anchorY, anchorZ;
    public boolean lit = true;

    public boolean textured = true;
    public TextureType texture = TextureType.NONE;

    protected boolean inStage = true;

    public boolean depthMask = true;

    public abstract void render(RenderRequest request, Renderer renderer);

    protected void doRender(Renderer renderer, int vbo, int tcbo, float[] vertices) {
        glPushMatrix();
        glDepthMask(this.depthMask);
        glTranslatef(this.x, this.y, this.z);
        glTranslatef(this.anchorX, this.anchorY, this.anchorZ);
        glRotatef(this.rotX, 1, 0, 0);
        glRotatef(this.rotY, 0, 1, 0);
        glRotatef(this.rotZ, 0, 0, 1);
        glTranslatef(-this.anchorX, -this.anchorY, -this.anchorZ);
        glScalef(this.scaleX, this.scaleY * -1, this.scaleZ);

        if (!this.inStage && this.lit) {
            glEnable(GL_LIGHTING);
        } else if (!this.inStage) {
            glDisable(GL_LIGHTING);
        }

        if (this.textured) {
            glEnable(GL_TEXTURE_2D);
            if (this.texture == TextureType.ALL) {
                glBindTexture(GL_TEXTURE_2D, renderer.owner.shadowTexture);
                glUseProgram(0);
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            } else {
                glBindTexture(GL_TEXTURE_2D, renderer.owner.skinFboTex);
                glUseProgram(renderer.owner.textureFilterProgram);
                glEnable(GL_BLEND);
                glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
            }

            glDisable(GL_ALPHA_TEST);
        } else {
            glDisable(GL_TEXTURE_2D);
            glUseProgram(0);
        }

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        if (tcbo == Integer.MAX_VALUE) {
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glTexCoordPointer(2, GL_FLOAT, 20, 12);
            glVertexPointer(3, GL_FLOAT, 20, 0);
        } else {
            glEnableClientState(GL_NORMAL_ARRAY);
            glBindBuffer(GL_ARRAY_BUFFER, tcbo);
            glTexCoordPointer(2, GL_FLOAT, 0, 0);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glVertexPointer(3, GL_FLOAT, 24, 0);
            glNormalPointer(GL_FLOAT, 24, 12);
        }

        if (tcbo == Integer.MAX_VALUE) {
            glDrawArrays(GL_QUADS, 0, vertices.length / 5);
        } else {
            glDrawArrays(GL_QUADS, 0, vertices.length / 6);
        }

        glDepthMask(true);
        glPopMatrix();
    }
}
