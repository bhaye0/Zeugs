package org.example;

import java.util.LinkedList;

public class PointListWrapper {
    private LinkedList<Point> points;

    public PointListWrapper() {
        this.points = new LinkedList<Point>();
    }

    public void add(Point point) {
        points.add(point);
    }

    public void remove(int i) {
        points.remove(i);
    }

    public void get(int i) {
        points.get(i);
    }

    public void getPointTriple(int i) {

    }

    public Point searchPoint(int x, int y) {
        return null;
    }
}
