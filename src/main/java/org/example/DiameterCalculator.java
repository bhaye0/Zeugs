package org.example;

import java.util.LinkedList;
import java.util.ListIterator;

public class DiameterCalculator {
    public static PointPair getDiameter(LinkedList<Point> convexHullList) {
        int size = convexHullList.size();
        PointPair diameter;

        switch(size) {
            case 0: diameter = new PointPair();
            case 1:
            case 2: diameter = getSimplePointDiameter(convexHullList, size); break;
            default: diameter = getComplexDiameter(convexHullList);
        }

        diameter.printToConsole();
        return diameter;
    }

    private static PointPair getSimplePointDiameter(LinkedList<Point> convexHullList, int size) {
        PointPair pointPair = new PointPair(
                convexHullList.getFirst(),
                (size == 1) ? convexHullList.getFirst() : convexHullList.getLast()
        );

        return pointPair;
    }

    private static PointPair getComplexDiameter(LinkedList<Point> convexHullList) {
        PointPair resultPointPair = new PointPair();
        PointPair startPointPair = getStartPointPair(convexHullList);
        PointPair currentPointPair = startPointPair.copy();

        int indexOfpMin = convexHullList.indexOf(startPointPair.p1);
        int indexOfpMax = convexHullList.indexOf(startPointPair.p2);

        ListIterator<Point> pointIteratorFromMin = convexHullList.listIterator(indexOfpMin);
        ListIterator<Point> pointIteratorFromMax = convexHullList.listIterator(indexOfpMax);

        boolean setMinNext = false;
        boolean setMaxNext = false;

        while(currentPointPair.p2 != startPointPair.p1 && currentPointPair.p1 != startPointPair.p2) {
            int indexCurrentA = (pointIteratorFromMax.hasNext()) ? pointIteratorFromMax.nextIndex() : 0;
            Point a = convexHullList.get(indexCurrentA);
            Point nachA = (indexCurrentA + 1 < convexHullList.size()) ?
                    convexHullList.get(indexCurrentA + 1) : convexHullList.get(0);

            int indexCurrentB = (pointIteratorFromMin.hasNext()) ? pointIteratorFromMin.nextIndex() : 0;
            Point b = convexHullList.get(indexCurrentB);
            Point nachB = (indexCurrentB + 1 < convexHullList.size()) ?
                    convexHullList.get(indexCurrentB + 1) : convexHullList.get(0);

            int x = b.x + a.x - nachB.x;
            int y = b.y + a.y - nachB.y;
            Point c = new Point(x,y);

            // a, nachA, c

            int wvt = calcWVT(convexHullList, a, c, nachA);

            if(wvt > 0) {
                wvtBiggerZero(convexHullList, pointIteratorFromMax, currentPointPair);
            }
            else if(wvt < 0) {
                wvtSmallerZero(convexHullList, pointIteratorFromMin, currentPointPair);
            }
            else if(wvt == 0) {
                long lengthDiameterFirstPair = ((a.x - nachA.x) * (a.x - nachA.x)) + ((a.y - nachA.y) * (a.y - nachA.y));
                long lengthDiameterSecondPair = ((b.x - nachB.x) * (b.x - nachB.x)) + ((startPointPair.p2.y - nachB.y) * (startPointPair.p2.y - nachB.y));

                if(lengthDiameterFirstPair < lengthDiameterSecondPair) {
                    if(!pointIteratorFromMax.hasNext()) {
                        pointIteratorFromMax = convexHullList.listIterator(convexHullList.indexOf(currentPointPair.p2));
                    }

                    setMinNext = true;
                    currentPointPair.p2 = pointIteratorFromMax.next();
                }
                else{
                    if(!pointIteratorFromMin.hasNext()) {
                        pointIteratorFromMin = convexHullList.listIterator(convexHullList.indexOf(currentPointPair.p1));
                    }

                    setMaxNext = true;
                    currentPointPair.p1 = pointIteratorFromMin.next();
                }
            }

            currentPointPair = getBiggerDiameter(resultPointPair, currentPointPair);

            if(setMinNext) {
                setMinNext = false;
                wvtSmallerZero(convexHullList, pointIteratorFromMin, currentPointPair);

                currentPointPair = getBiggerDiameter(resultPointPair, currentPointPair);
            }

            if(setMaxNext) {
                if(!pointIteratorFromMax.hasNext()) {
                    pointIteratorFromMax = convexHullList.listIterator(convexHullList.indexOf(currentPointPair.p2));
                }

                setMaxNext = false;
                currentPointPair.p2 = pointIteratorFromMax.next();
                currentPointPair = getBiggerDiameter(resultPointPair, currentPointPair);
            }
        }

        while (currentPointPair.p2 == startPointPair.p1 && currentPointPair.p1 != startPointPair.p2) {
            if(!pointIteratorFromMin.hasNext()) {
                pointIteratorFromMin = convexHullList.listIterator(convexHullList.indexOf(currentPointPair.p1));
            }

            currentPointPair.p1 = pointIteratorFromMin.next();
            resultPointPair = getBiggerDiameter(resultPointPair, currentPointPair);
        }

        while (currentPointPair.p2 != startPointPair.p1 && currentPointPair.p1 == startPointPair.p2) {
            if(!pointIteratorFromMax.hasNext()) {
                pointIteratorFromMax = convexHullList.listIterator(convexHullList.indexOf(currentPointPair.p2));
            }

            currentPointPair.p2 = pointIteratorFromMax.next();
            resultPointPair = getBiggerDiameter(resultPointPair, currentPointPair);
        }

        return resultPointPair;
    }

    private static PointPair getStartPointPair(LinkedList<Point> convexHullList) {
        PointPair startPointPair = new PointPair(
                convexHullList.getFirst(),
                convexHullList.getFirst()
        );

        for(Point point : convexHullList) {
            if(point.comparedTo(startPointPair.p2) == 1) startPointPair.p2 = point;
            if(point.comparedTo(startPointPair.p1) == -1) startPointPair.p1 = point;
        }
        return startPointPair;
    }

    private static void wvtSmallerZero(LinkedList<Point> convexHullList, ListIterator<Point> pointIteratorFromMin, PointPair currentPointPair) {
        if(pointIteratorFromMin.hasNext()) {
            if(pointIteratorFromMin.nextIndex() == 0) {
                pointIteratorFromMin.next();
            }
            currentPointPair.p1 = pointIteratorFromMin.next();
        }
        else {
            currentPointPair.p1 = convexHullList.get(0);
            pointIteratorFromMin = convexHullList.listIterator(convexHullList.indexOf(currentPointPair.p1));
        }
    }

    private static void wvtBiggerZero(LinkedList<Point> convexHullList, ListIterator<Point> pointIteratorFromMax, PointPair currentPointPair) {
        if(pointIteratorFromMax.hasNext()) {
            currentPointPair.p2 = pointIteratorFromMax.next();
        }
        else {
            currentPointPair.p2 = convexHullList.get(0);
            int pMaxIndex = convexHullList.indexOf(currentPointPair.p2);
            pointIteratorFromMax = convexHullList.listIterator(pMaxIndex);
        }
    }

    private static PointPair getBiggerDiameter(PointPair resultPointPair, PointPair currentPointPair) {
        int resultDistance = resultPointPair.calcSquaredDistance();
        int currentDistance = currentPointPair.calcSquaredDistance();

        return (currentDistance > resultDistance) ? currentPointPair.copy() : resultPointPair;
    }

    private static int calcWVT(LinkedList<Point> convexHullList, Point a, Point c, Point nachA) {
        return ((a.x) * (nachA.y - c.y)) + ((nachA.x) * (c.y - a.y)) + ((c.x) * (a.y - nachA.y));
    }

    public PointPair getDiameterSimpleWay(LinkedList<Point> convexHullList) {
        int maxDistance = 0;
        PointPair diameter = new PointPair();
        PointPair currentPointPair = new PointPair();

        for (int i = 0; i < convexHullList.size(); i++) {
            for (int j = i + 1; j < convexHullList.size(); j++) {
                currentPointPair.p1 = convexHullList.get(i);
                currentPointPair.p2 = convexHullList.get(j);
                int currentDistance = currentPointPair.calcSquaredDistance();

                if (currentDistance > maxDistance) {
                    maxDistance = currentDistance;
                    diameter = currentPointPair.copy();
                }
            }
        }

        return diameter;

    }
}
