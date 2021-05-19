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

public enum RenderMode {

    FACE(256 * 2, 256 * 2, -90, 0),
    FRONT(256 * 2, 256 * 2, -90, 0), // TODO there are some pixels at the top right leg that shouldn't be there
    FRONT_FULL(256 * 2, 256 * 2, -90, 0), // TODO this sometimes doesn't work properly

    HEAD(256 * 2, 256 * 2, -35, -20),
    BUST(256 * 2, 256 * 2, 20, -10),
    FULL(158 * 3, 256 * 3, 20, -10);

    private static final RenderMode[] DIMENSION_3 = new RenderMode[]{HEAD, BUST, FULL};
    private static final RenderMode[] DIMENSION_2 = new RenderMode[]{FACE, FRONT, FRONT_FULL};
    private static final RenderMode[] BODY = new RenderMode[]{FRONT, FRONT_FULL, BUST, FULL};

    private final int defaultWidth;
    private final int defaultHeight;
    private final int defaultRotationX;
    private final int defaultRotationY;

    RenderMode(int defaultWidth, int defaultHeight, int defaultRotationX, int defaultRotationY) {
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.defaultRotationX = defaultRotationX;
        this.defaultRotationY = defaultRotationY;
    }

    public int getDefaultHeight() {
        return this.defaultHeight;
    }

    public int getDefaultWidth() {
        return this.defaultWidth;
    }

    public int getDefaultRotationX() {
        return this.defaultRotationX;
    }

    public int getDefaultRotationY() {
        return this.defaultRotationY;
    }

    public boolean isTall() {
        return this == FULL || this == FRONT_FULL;
    }

    public boolean is3D() {
        return this == HEAD || this == BUST || this == FULL;
    }

    public static RenderMode[] body() {
        return BODY;
    }

    public static RenderMode[] dimension(int dimension) {
        switch (dimension) {
            case 2:
                return DIMENSION_2;
            case 3:
                return DIMENSION_3;
            default:
                throw new IllegalStateException("Unknown dimension: " + dimension);
        }
    }
}
