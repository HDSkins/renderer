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

public class Vertices {

    public static float[] BACK_VERTICES = new float[]{
            // Back
            -1.0f, -1.0f, -1.0f,
            0, 0, -1,
            1.0f, -1.0f, -1.0f,
            0, 0, -1,
            1.0f, 1.0f, -1.0f,
            0, 0, -1,
            -1.0f, 1.0f, -1.0f,
            0, 0, -1,
            // Front
            -1.0f, -1.0f, 1.0f,
            0, 0, 1,
            1.0f, -1.0f, 1.0f,
            0, 0, 1,
            1.0f, 1.0f, 1.0f,
            0, 0, 1,
            -1.0f, 1.0f, 1.0f,
            0, 0, 1,
            // Top
            1.0f, 1.0f, -1.0f,
            0, 1, 0,
            -1.0f, 1.0f, -1.0f,
            0, 1, 0,
            -1.0f, 1.0f, 1.0f,
            0, 1, 0,
            1.0f, 1.0f, 1.0f,
            0, 1, 0,
            // Bottom
            1.0f, -1.0f, 1.0f,
            0, -1, 0,
            -1.0f, -1.0f, 1.0f,
            0, -1, 0,
            -1.0f, -1.0f, -1.0f,
            0, -1, 0,
            1.0f, -1.0f, -1.0f,
            0, -1, 0,
            // Right
            -1.0f, -1.0f, -1.0f,
            -1, 0, 0,
            -1.0f, -1.0f, 1.0f,
            -1, 0, 0,
            -1.0f, 1.0f, 1.0f,
            -1, 0, 0,
            -1.0f, 1.0f, -1.0f,
            -1, 0, 0,
            // Left
            1.0f, -1.0f, 1.0f,
            1, 0, 0,
            1.0f, -1.0f, -1.0f,
            1, 0, 0,
            1.0f, 1.0f, -1.0f,
            1, 0, 0,
            1.0f, 1.0f, 1.0f,
            1, 0, 0,
    };

    public static float[] FRONT_VERTICES = new float[]{
            // Front
            -1.0f, -1.0f, 1.0f,
            0, 0, 1,
            1.0f, -1.0f, 1.0f,
            0, 0, 1,
            1.0f, 1.0f, 1.0f,
            0, 0, 1,
            -1.0f, 1.0f, 1.0f,
            0, 0, 1,
            // Back
            -1.0f, -1.0f, -1.0f,
            0, 0, -1,
            1.0f, -1.0f, -1.0f,
            0, 0, -1,
            1.0f, 1.0f, -1.0f,
            0, 0, -1,
            -1.0f, 1.0f, -1.0f,
            0, 0, -1,
            // Top
            -1.0f, 1.0f, 1.0f,
            0, 1, 0,
            1.0f, 1.0f, 1.0f,
            0, 1, 0,
            1.0f, 1.0f, -1.0f,
            0, 1, 0,
            -1.0f, 1.0f, -1.0f,
            0, 1, 0,
            // Bottom
            -1.0f, -1.0f, -1.0f,
            0, -1, 0,
            1.0f, -1.0f, -1.0f,
            0, -1, 0,
            1.0f, -1.0f, 1.0f,
            0, -1, 0,
            -1.0f, -1.0f, 1.0f,
            0, -1, 0,
            // Left
            1.0f, -1.0f, 1.0f,
            1, 0, 0,
            1.0f, -1.0f, -1.0f,
            1, 0, 0,
            1.0f, 1.0f, -1.0f,
            1, 0, 0,
            1.0f, 1.0f, 1.0f,
            1, 0, 0,
            // Right
            -1.0f, -1.0f, -1.0f,
            -1, 0, 0,
            -1.0f, -1.0f, 1.0f,
            -1, 0, 0,
            -1.0f, 1.0f, 1.0f,
            -1, 0, 0,
            -1.0f, 1.0f, -1.0f,
            -1, 0, 0
    };

    public static float[] PLANE_VERTICES = new float[]{
            -1.0f, 0.0f, 1.0f,
            0, 1, 0,
            1.0f, 0.0f, 1.0f,
            0, 1, 0,
            1.0f, 0.0f, -1.0f,
            0, 1, 0,
            -1.0f, 0.0f, -1.0f,
            0, 1, 0,
    };

}
