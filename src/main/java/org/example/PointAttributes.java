package org.example;

public class PointAttributes {
    public int x;
    public int y;
    public Boolean isExtremePoint = false;
    public Boolean removeMarker = false;

    public PointAttributes(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int comparedTo(PointAttributes p2) {
        if (x > p2.x) return 1;
        else if (x < p2.x) return -1;
        else {
            if (y > p2.y) return 1;
            if (x < p2.y) return -1;
            else return 0;
        }
    }
}
