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

import de.hdskins.skinrenderer.RenderContext;
import de.hdskins.skinrenderer.RenderRequest;
import de.hdskins.skinrenderer.render.primitive.Group;
import de.hdskins.skinrenderer.render.primitive.PrimitiveBuilder;

public class FlatBodyRenderer extends Renderer {

    public FlatBodyRenderer(RenderContext owner) {
        super(owner);
    }

    @Override
    protected void initPrimitives(RenderRequest request) {
        Group group = PrimitiveBuilder.group()
                .executeIf(
                        request.isFull(),
                        builder -> builder.y(request.isFlipped() ? 1.5f : -1.5f).z(-9.75f),
                        builder -> builder.y(request.isFlipped() ? 0.04f : -0.04f).z(-6.25f)
                )
                .rotZ(0).rotY(request.isFlipped() ? 180 : 0).rotX(request.getRotationX())
                .lit(false)
                .addTo(this);

        // head
        PrimitiveBuilder.plane()
                .x(0).z(-1.5f)
                .texture(TextureType.HEAD_FRONT)
                .addTo(group);
        PrimitiveBuilder.plane()
                .z(-1.5001f)
                .scale(1.05f)
                .texture(TextureType.HEAD_FRONT_OVERLAY)
                .depthMask(false)
                .addTo(group);

        // body
        PrimitiveBuilder.plane()
                .x(0).z(1f)
                .scaleZ(1.5f)
                .texture(TextureType.BODY_FRONT)
                .addTo(group);
        PrimitiveBuilder.plane()
                .z(0.9999f)
                .scale(1.05f, 1.05f, 1.55f)
                .texture(TextureType.BODY_FRONT_OVERLAY)
                .depthMask(false)
                .addTo(group);

        // right arm
        PrimitiveBuilder.plane()
                .x(request.isSlim() ? -1.375f : -1.5f).z(1f)
                .scaleX(request.isSlim() ? 0.375f : 0.5f).scaleZ(1.5f)
                .texture(request.isSlim() ? TextureType.RARM_SLIM_FRONT : TextureType.RARM_FRONT)
                .addTo(group);
        PrimitiveBuilder.plane()
                .x(request.isSlim() ? -1.375f : -1.5f).z(0.9999f)
                .scaleX(request.isSlim() ? 0.425f : 0.55f).scaleZ(1.55f)
                .texture(request.isSlim() ? TextureType.RARM_SLIM_FRONT_OVERLAY : TextureType.RARM_FRONT_OVERLAY)
                .depthMask(false)
                .addTo(group);

        // left arm
        PrimitiveBuilder.plane()
                .x(request.isSlim() ? 1.375f : 1.5f).z(1f)
                .scaleX(request.isSlim() ? 0.375f : 0.5f).scaleZ(1.5f)
                .texture(request.isSlim() ? TextureType.LARM_SLIM_FRONT : TextureType.LARM_FRONT)
                .addTo(group);
        PrimitiveBuilder.plane()
                .x(request.isSlim() ? 1.375f : 1.5f).z(0.9999f)
                .scaleX(request.isSlim() ? 0.425f : 0.55f).scaleZ(1.55f)
                .texture(request.isSlim() ? TextureType.LARM_SLIM_FRONT_OVERLAY : TextureType.LARM_FRONT_OVERLAY)
                .depthMask(false)
                .addTo(group);

        // left leg
        PrimitiveBuilder.plane()
                .x(0.5f).z(4f)
                .scaleX(0.5f).scaleZ(1.5f)
                .texture(TextureType.LLEG_FRONT)
                .addTo(group);
        PrimitiveBuilder.plane()
                .x(0.4999f).z(3.9999f)
                .scaleX(1.55f).scaleZ(0.55f)
                .texture(TextureType.LLEG_FRONT_OVERLAY)
                .depthMask(false)
                .addTo(group);

        // right leg
        PrimitiveBuilder.plane()
                .x(-0.5f).z(5f)
                .scaleX(0.5f).scaleZ(1.5f)
                .texture(TextureType.RLEG_FRONT)
                .addTo(group);
        PrimitiveBuilder.plane()
                .x(-0.4999f).z(3.9999f)
                .scaleX(0.55f).scaleZ(1.55f)
                .texture(TextureType.RLEG_FRONT_OVERLAY)
                .depthMask(false)
                .addTo(group);
    }

}
