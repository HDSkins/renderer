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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

// TODO Slim is using the wrong position for the arms
public enum TextureType {

    NONE,
    ALL(
            0, 0, 64, 64
    ),
    HEAD(
            // Front (Red)
            8, 8, 8, 8,
            // Back (Blue)
            24, 8, 8, 8,
            // Top (Purple)
            8, 0, 8, 8,
            // Bottom (Gray)
            16, 0, 8, 8,
            // Left (Yellow)
            16, 8, 8, 8,
            // Right (Green)
            0, 8, 8, 8
    ),
    HEAD_OVERLAY(
            // Front (Red)
            40, 8, 8, 8,
            // Back (Blue)
            56, 8, 8, 8,
            // Top (Purple)
            40, 0, 8, 8,
            // Bottom (Gray)
            48, 0, 8, 8,
            // Left (Yellow)
            48, 8, 8, 8,
            // Right (Green)
            32, 8, 8, 8
    ),
    BODY(
            // Front (Red)
            20, 20, 8, 12,
            // Back (Blue)
            32, 20, 8, 12,
            // Top (Purple)
            20, 16, 8, 4,
            // Bottom (Gray)
            28, 16, 8, 4,
            // Left (Yellow)
            28, 20, 4, 12,
            // Right (Green)
            16, 20, 4, 12
    ),
    BODY_OVERLAY(
            // Front (Red)
            20, 36, 8, 12,
            // Back (Blue)
            32, 36, 8, 12,
            // Top (Purple)
            20, 32, 8, 4,
            // Bottom (Gray)
            28, 32, 8, 4,
            // Left (Yellow)
            28, 36, 4, 12,
            // Right (Green)
            16, 36, 4, 12
    ),
    RLEG(
            // Front (Red)
            4, 20, 4, 12,
            // Back (Blue)
            12, 20, 4, 12,
            // Top (Purple)
            4, 16, 4, 4,
            // Bottom (Gray)
            8, 16, 4, 4,
            // Left (Yellow)
            8, 20, 4, 12,
            // Right (Green)
            0, 20, 4, 12
    ),
    RLEG_OVERLAY(
            // Front (Red)
            4, 36, 4, 12,
            // Back (Blue)
            12, 36, 4, 12,
            // Top (Purple)
            4, 32, 4, 4,
            // Bottom (Gray)
            8, 32, 4, 4,
            // Left (Yellow)
            8, 36, 4, 12,
            // Right (Green)
            0, 36, 4, 12
    ),
    LLEG(
            // Front (Red)
            20, 52, 4, 12,
            // Back (Blue)
            28, 52, 4, 12,
            // Top (Purple)
            20, 48, 4, 4,
            // Bottom (Gray)
            24, 48, 4, 4,
            // Left (Yellow)
            24, 52, 4, 12,
            // Right (Green)
            16, 52, 4, 12
    ),
    LLEG_OVERLAY(
            // Front (Red)
            4, 52, 4, 12,
            // Back (Blue)
            12, 52, 4, 12,
            // Top (Purple)
            4, 48, 4, 4,
            // Bottom (Gray)
            8, 48, 4, 4,
            // Left (Yellow)
            8, 52, 4, 12,
            // Right (Green)
            0, 52, 4, 12
    ),
    RARM(
            // Front (Red)
            44, 20, 4, 12,
            // Back (Blue)
            52, 20, 4, 12,
            // Top (Purple)
            44, 16, 4, 4,
            // Bottom (Gray)
            48, 16, 4, 4,
            // Left (Yellow)
            48, 20, 4, 12,
            // Right (Green)
            40, 20, 4, 12
    ),
    RARM_SLIM(
            // Front (Red)
            44, 20, 3, 12,
            // Back (Blue)
            51, 20, 3, 12,
            // Top (Purple)
            44, 16, 3, 4,
            // Bottom (Gray)
            47, 16, 3, 4,
            // Left (Yellow)
            47, 20, 4, 12,
            // Right (Green)
            40, 20, 4, 12
    ),
    RARM_OVERLAY(
            // Front (Red)
            44, 36, 4, 12,
            // Back (Blue)
            52, 36, 4, 12,
            // Top (Purple)
            44, 32, 4, 4,
            // Bottom (Gray)
            48, 32, 4, 4,
            // Left (Yellow)
            48, 36, 4, 12,
            // Right (Green)
            40, 36, 4, 12
    ),
    RARM_SLIM_OVERLAY(
            // Front (Red)
            44, 36, 3, 12,
            // Back (Blue)
            51, 36, 3, 12,
            // Top (Purple)
            44, 32, 3, 4,
            // Bottom (Gray)
            47, 32, 3, 4,
            // Left (Yellow)
            47, 36, 4, 12,
            // Right (Green)
            40, 36, 4, 12
    ),
    LARM(
            // Front (Red)
            36, 52, 4, 12,
            // Back (Blue)
            44, 52, 4, 12,
            // Top (Purple)
            36, 48, 4, 4,
            // Bottom (Gray)
            40, 48, 4, 4,
            // Left (Yellow)
            40, 52, 4, 12,
            // Right (Green)
            32, 52, 4, 12
    ),
    LARM_SLIM(
            // Front (Red)
            36, 52, 3, 12,
            // Back (Blue)
            43, 52, 3, 12,
            // Top (Purple)
            36, 48, 3, 4,
            // Bottom (Gray)
            39, 48, 3, 4,
            // Left (Yellow)
            39, 52, 4, 12,
            // Right (Green)
            32, 52, 4, 12
    ),
    LARM_OVERLAY(
            // Front (Red)
            52, 52, 4, 12,
            // Back (Blue)
            60, 52, 4, 12,
            // Top (Purple)
            52, 48, 4, 4,
            // Bottom (Gray)
            56, 48, 4, 4,
            // Left (Yellow)
            58, 52, 4, 12,
            // Right (Green)
            48, 52, 4, 12
    ),
    LARM_SLIM_OVERLAY(
            // Front (Red)
            52, 52, 3, 12,
            // Back (Blue)
            59, 52, 3, 12,
            // Top (Purple)
            52, 48, 3, 4,
            // Bottom (Gray)
            55, 48, 3, 4,
            // Left (Yellow)
            55, 52, 4, 12,
            // Right (Green)
            48, 52, 4, 12
    ),

    HEAD_FRONT(
            8, 8, 8, 8
    ),
    HEAD_FRONT_OVERLAY(
            40, 8, 8, 8
    ),
    BODY_FRONT(
            20, 20, 8, 12
    ),
    BODY_FRONT_OVERLAY(
            20, 36, 8, 12
    ),
    RLEG_FRONT(
            4, 20, 4, 12
    ),
    RLEG_FRONT_OVERLAY(
            4, 36, 4, 12
    ),
    LLEG_FRONT(
            20, 52, 4, 12
    ),
    LLEG_FRONT_OVERLAY(
            4, 52, 4, 12
    ),
    RARM_FRONT(
            44, 20, 4, 12
    ),
    RARM_SLIM_FRONT(
            45, 20, 3, 12
    ),
    RARM_FRONT_OVERLAY(
            44, 36, 4, 12
    ),
    RARM_SLIM_FRONT_OVERLAY(
            45, 36, 3, 12
    ),
    LARM_FRONT(
            36, 52, 4, 12
    ),
    LARM_SLIM_FRONT(
            36, 52, 3, 12
    ),
    LARM_FRONT_OVERLAY(
            52, 52, 4, 12
    ),
    LARM_SLIM_FRONT_OVERLAY(
            52, 52, 3, 12
    );

    public final int[] rawCoords;
    public final float[] u = new float[24];
    public final float[] v = new float[24];

    private static final BiMap<TextureType, TextureType> OPPOSITES = HashBiMap.create();

    private static final int WIDTH = 64;
    private static final int HEIGHT = 64;

    static {
        OPPOSITES.put(RARM, LARM);
        OPPOSITES.put(RARM_OVERLAY, LARM_OVERLAY);
        OPPOSITES.put(RARM_SLIM, LARM_SLIM);
        OPPOSITES.put(RARM_SLIM_OVERLAY, LARM_SLIM_OVERLAY);
        OPPOSITES.put(RLEG, LLEG);
        OPPOSITES.put(RLEG_OVERLAY, LLEG_OVERLAY);
    }

    TextureType() {
        this.rawCoords = null;
    }

    /**
     * constructor uses varargs for compactness
     * <p>
     * arguments are effectively:
     * <side>_x, <side>_y, <side>_width, <side>_height
     * where <side> is the face in question, in order:
     * Front, Back, Top, Bottom, Left, Right
     */
    TextureType(int... assorted) {
        this.rawCoords = assorted;

        for (int i = 0; i < assorted.length / 4; i++) {
            int idx = i * 4;

            int x = assorted[idx];
            int y = assorted[idx + 1];
            int edgeX = x + assorted[idx + 2];
            int edgeY = y + assorted[idx + 3];

            // slightly shrink the box to prevent texture bleeding

            this.u[idx] = this.div(WIDTH, x) + 0.001f;
            this.v[idx] = this.div(HEIGHT, edgeY) - 0.001f;

            this.u[idx + 1] = this.div(WIDTH, edgeX) - 0.001f;
            this.v[idx + 1] = this.div(HEIGHT, edgeY) - 0.001f;

            this.u[idx + 2] = this.div(WIDTH, edgeX) - 0.001f;
            this.v[idx + 2] = this.div(HEIGHT, y) + 0.001f;

            this.u[idx + 3] = this.div(WIDTH, x) + 0.001f;
            this.v[idx + 3] = this.div(HEIGHT, y) + 0.001f;
        }
    }

    private float div(float max, float x) { // upcasting!
        return x / max;
    }

    public TextureType opposite() {
        TextureType opposite = OPPOSITES.get(this);
        if (opposite != null) {
            return opposite;
        }
        return OPPOSITES.inverse().get(this);
    }

}
