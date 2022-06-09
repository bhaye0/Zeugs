package org.example;

public class Point {
    public int x;
    public int y;
    public Boolean isExtremePoint = false;
    public Boolean removeMarker = false;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int comparedTo(Point p2) {
        if (x > p2.x) return 1;
        else if (x < p2.x) return -1;
        else {
            if (y > p2.y) return 1;
            if (x < p2.y) return -1;
            else return 0;
        }
    }

    public int squaredDistanceTo(Point p2) {
        int deltaX = (x - p2.x);
        int deltaY = (y - p2.y);

        return (deltaX * deltaX) + (deltaY * deltaY);
    }
}
