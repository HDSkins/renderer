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
import de.hdskins.skinrenderer.RenderRotation;
import de.hdskins.skinrenderer.render.primitive.Group;
import de.hdskins.skinrenderer.render.primitive.PrimitiveBuilder;
import de.hdskins.skinrenderer.request.RenderRequest;
import de.hdskins.skinrenderer.request.RenderRequestProperties;

public class BodyRenderer extends Renderer {

    public BodyRenderer(RenderContext owner) {
        super(owner);
    }

    @Override
    protected void initPrimitives(RenderRequest request, boolean back) {
        RenderRotation rotation = request.getProperty(RenderRequestProperties.ROTATION);
        float tilt = rotation.getY();
        float angle = rotation.getX();
        boolean flipped = request.getProperty(RenderRequestProperties.FLIPPED);
        boolean slim = request.getProperty(RenderRequestProperties.SLIM);

        float overlayScale = request.getProperty(RenderRequestProperties.OVERLAY_SCALE);
        boolean overlay = overlayScale > 0 && request.getProperty(RenderRequestProperties.OVERLAY);

        Group group = PrimitiveBuilder.group()
                .x(0).y(request.isFull() ? (flipped ? -2.7f : -2.8f) : -1f).z(request.isFull() ? -10.35f : -6f)
                .rotX(tilt).rotY(angle)
                .primitive();
        this.addPrimitive(group);

        if ((request.isFull() || flipped) && request.getProperty(RenderRequestProperties.SHADOW)) {
            // shadow
            PrimitiveBuilder.plane()
                    .y(request.isFull() ? (flipped ? 6.825f : 7f) : 2.85f)
                    .scaleX(1.85f).scaleZ(flipped ? 1.85f : 0.85f)
                    .texture(TextureType.ALL).lit(false)
                    .addTo(group);
        }

        Group group2 = flipped ? PrimitiveBuilder.group().rotZ(180).y((-group.y * 2) + (request.isFull() ? 0.3f : -0.25f)).addTo(group) : group;

        // head
        PrimitiveBuilder.cube().texture(TextureType.HEAD).addTo(group2);
        if (overlay) {
            PrimitiveBuilder.cube().scale(overlayScale).texture(TextureType.HEAD_OVERLAY).depthMask(false).addTo(group2);
        }

        // body
        PrimitiveBuilder.cube().y(2.5f).scaleY(1.5f).scaleZ(0.5f).texture(TextureType.BODY).addTo(group2);
        if (overlay) {
            PrimitiveBuilder.cube().y(2.5f).scale(overlayScale, 1.55f, 0.55f).texture(TextureType.BODY_OVERLAY).depthMask(false).addTo(group2);
        }

        // left arm
        PrimitiveBuilder.cube()
                .x(slim ? 1.375f : 1.5f).y(2.5f)
                .scale(slim ? 0.375f : 0.5f, 1.5f, 0.5f)
                .rotX(-rotation.getLegs())
                .rotZ(-10f)
                .texture(back, slim ? TextureType.RARM_SLIM : TextureType.RARM)
                .execute(builder -> builder.anchorX(-builder.primitive().scaleX).anchorY(-builder.primitive().scaleY))
                .addTo(group2);

        if (overlay) {
            PrimitiveBuilder.cube()
                    .x(slim ? 1.375f : 1.5f).y(2.5f)
                    .scale(slim ? 0.425f : 0.55f, 1.55f, 0.55f)
                    .rotX(-rotation.getLegs())
                    .rotZ(-10f)
                    .texture(back, slim ? TextureType.RARM_SLIM_OVERLAY : TextureType.RARM_OVERLAY)
                    .depthMask(false)
                    .execute(builder -> builder.anchorX(-builder.primitive().scaleX).anchorY(-builder.primitive().scaleY))
                    .addTo(group2);
        }

        // right arm
        PrimitiveBuilder.cube()
                .x(slim ? -1.375f : -1.5f).y(2.5f)
                .scale(slim ? 0.375f : 0.5f, 1.5f, 0.5f)
                .rotX(rotation.getLegs())
                .rotZ(10f)
                .texture(back, slim ? TextureType.LARM_SLIM : TextureType.LARM)
                .execute(builder -> builder.anchorX(builder.primitive().scaleX).anchorY(-builder.primitive().scaleY))
                .addTo(group2);

        if (overlay) {
            PrimitiveBuilder.cube()
                    .x(slim ? -1.375f : -1.5f).y(2.5f)
                    .scale(slim ? 0.425f : 0.55f, 1.55f, 0.55f)
                    .rotX(rotation.getLegs())
                    .rotZ(10f)
                    .texture(back, slim ? TextureType.LARM_SLIM_OVERLAY : TextureType.LARM_OVERLAY)
                    .depthMask(false)
                    .execute(builder -> builder.anchorX(builder.primitive().scaleX).anchorY(-builder.primitive().scaleY))
                    .addTo(group2);
        }

        // left leg
        PrimitiveBuilder.cube()
                .x(0.5f).y(5.5f)
                .rotX(rotation.getLegs())
                .scale(0.5f, 1.5f, 0.5f)
                .anchorY(-1.5f)
                .texture(back, TextureType.LLEG)
                .addTo(group2);

        if (overlay) {
            PrimitiveBuilder.cube()
                    .x(0.5f).y(5.5f)
                    .rotX(rotation.getLegs())
                    .scale(0.55f, 1.55f, 0.55f)
                    .anchorY(-1.55f)
                    .texture(back, TextureType.LLEG_OVERLAY)
                    .depthMask(false)
                    .addTo(group2);
        }

        // right leg
        PrimitiveBuilder.cube()
                .x(-0.5f).y(5.5f)
                .rotX(-rotation.getLegs())
                .scale(0.5f, 1.5f, 0.5f)
                .anchorY(-1.5f)
                .texture(back, TextureType.RLEG)
                .addTo(group2);

        if (overlay) {
            PrimitiveBuilder.cube()
                    .x(-0.5f).y(5.5f)
                    .rotX(-rotation.getLegs())
                    .scale(0.55f, 1.55f, 0.55f)
                    .anchorY(-1.55f)
                    .texture(back, TextureType.RLEG_OVERLAY)
                    .depthMask(false)
                    .addTo(group2);
        }
    }

}