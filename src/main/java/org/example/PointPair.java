package org.example;

public class PointPair {
    public PointAttributes pMin;
    public PointAttributes pMax;

    public PointPair() {

    }

    public int[][] getDiameterArray(){
        int[][] pointPair = new int[2][2];
        pointPair[0][0] = pMin.x;
        pointPair[0][1] = pMin.y;
        pointPair[1][0] = pMax.x;
        pointPair[1][1] = pMax.y;

        return pointPair;
    }

    public void printToConsole() {
        System.out.println("pMin " + "[" + pMin.x + pMin.y + "]" + ", pMin " + "[" + pMax.x + pMax.y + "]");
    }

    public int calcDistanceSquared() {
        int deltaX = (pMax.x - pMin.x);
        int deltaY = (pMax.y - pMin.y);

        return (deltaX * deltaX) + (deltaY * deltaY);
    }

    public PointPair copy() {
        PointPair pointPair = new PointPair();
        pointPair.pMin = this.pMin;
        pointPair.pMax = this.pMax;
        return pointPair;
    }
}
