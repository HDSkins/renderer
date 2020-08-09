package de.hdskins.skinrenderer;

public class RenderRotation {

    private int x;
    private int y;
    private int legs;

    public RenderRotation(int x, int y, int legs) {
        this.x = x;
        this.y = y;
        this.legs = legs;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getLegs() {
        return this.legs;
    }

    public void setLegs(int legs) {
        this.legs = legs;
    }
}
