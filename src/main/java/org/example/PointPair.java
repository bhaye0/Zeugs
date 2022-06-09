package org.example;

public class PointPair {
    public Point p1;
    public Point p2;

    public PointPair(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public PointPair() {
        this.p1 = new Point(0,0);
        this.p2 = new Point(0,0);
    }

    public int[][] toArray(){
        int[][] pointPair = new int[2][2];
        pointPair[0][0] = p1.x;
        pointPair[0][1] = p1.y;
        pointPair[1][0] = p2.x;
        pointPair[1][1] = p2.y;

        return pointPair;
    }

    public void printToConsole() {
        System.out.println("pMin " + "[" + p1.x + p1.y + "]" + ", pMin " + "[" + p2.x + p2.y + "]");
    }

    public int calcSquaredDistance() {
        int deltaX = (p2.x - p1.x);
        int deltaY = (p2.y - p1.y);

        return (deltaX * deltaX) + (deltaY * deltaY);
    }

    public PointPair copy() {
        PointPair pointPair = new PointPair();
        pointPair.p1 = this.p1;
        pointPair.p2 = this.p2;
        return pointPair;
    }
}
