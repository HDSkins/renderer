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

public class HeadRenderer extends Renderer {

    public HeadRenderer(RenderContext owner) {
        super(owner);
    }

    @Override
    protected void initPrimitives(RenderRequest request, boolean back) {
        RenderRotation rotation = request.getProperty(RenderRequestProperties.ROTATION);
        float tilt = rotation.getY();
        float angle = rotation.getX();

        float overlayScale = request.getProperty(RenderRequestProperties.OVERLAY_SCALE);
        boolean overlay = overlayScale > 0 && request.getProperty(RenderRequestProperties.OVERLAY);

        Group group = PrimitiveBuilder.group()
                .y(-0.25f).z(-5f)
                .rotX(tilt).rotY(angle)
                .addTo(this);

        if (request.getProperty(RenderRequestProperties.SHADOW)) {
            // shadow
            PrimitiveBuilder.plane()
                    .y(1)
                    .scaleX(1.95f).scaleZ(1.95f)
                    .texture(TextureType.ALL)
                    .lit(false)
                    .addTo(group);
        }

        // head
        PrimitiveBuilder.cube()
                .y(-0.025f).z(-0.025f)
                .texture(TextureType.HEAD)
                .executeIf(request.getProperty(RenderRequestProperties.FLIPPED), builder -> builder.rotZ(180))
                .addTo(group);

        if (overlay) {
            PrimitiveBuilder.cube()
                    .scale(overlayScale)
                    .executeIf(request.getProperty(RenderRequestProperties.FLIPPED), builder -> builder.rotZ(180))
                    .texture(TextureType.HEAD_OVERLAY)
                    .depthMask(false)
                    .addTo(group);
        }
    }
}
