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

import com.google.common.collect.Lists;
import de.hdskins.skinrenderer.render.Renderer;
import de.hdskins.skinrenderer.request.RenderRequest;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Group extends Primitive {

    public final List<Primitive> members = Lists.newArrayList();

    @Override
    public void render(RenderRequest request, boolean back, Renderer renderer) {
        glPushMatrix();
        glTranslatef(this.x, this.y, this.z);
        glRotatef(this.rotX, 1.0f, 0.0f, 0.0f);
        glRotatef(this.rotY, 0.0f, 1.0f, 0.0f);
        glRotatef(this.rotZ, 0.0f, 0.0f, 1.0f);
        glScalef(this.scaleX, this.scaleY, this.scaleZ);

        if (this.lit) {
            glEnable(GL_LIGHTING);
        } else {
            glDisable(GL_LIGHTING);
        }

        for (Primitive p : this.members) {
            p.inStage = true;
            p.render(request, back, renderer);
        }

        glPopMatrix();
    }

}
