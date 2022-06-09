package org.example;

import java.util.LinkedList;
import java.util.ListIterator;

public class ConvexHullCalculator {
    LinkedList<Point> contourPolygonList;
    LinkedList<Point> convexHullList;
    LinkedList<Point> removeList ;
    int[][] convexHull;

    public static int[][] getConvexHull(
            LinkedList<Point> contourPolygonList,
            LinkedList<Point> convexHullList,
            LinkedList<Point> removeList
    ) {

        int[][] sortedPoints = setOfPoints.getSortedArray();
        ContourPolygon contourPolygon = new ContourPolygon();
        contourPolygonList = contourPolygon.calculateContourPolygon(sortedPoints);

        if (contourPolygonList.size() <= 2) {
            convexHullList = contourPolygonList;
        }
        else {

            convexHullList = rekursiveBerechnung(contourPolygonList);
        }
        int i = 0;
        convexHull = new int[convexHullList.size()][2];


        for(Point p : convexHullList) {
            convexHull[i][0] = p.x;
            convexHull[i][1] = p.y;
            i++;

        }

        return convexHull;
    }

    private static LinkedList<Point> rekursiveBerechnung(LinkedList<Point> contourPolygonList) {
        ListIterator<Point> listPositionIterator = this.contourPolygonList.listIterator();
        Point a, b, c;

        while (listPositionIterator.hasNext()) {
            listPositionIterator.next();

            int currentIndex = listPositionIterator.previousIndex();

            a = contourPolygonList.get(currentIndex);

            int bIndex = getNextUnmarkedPoint(currentIndex + 1);
            b = contourPolygonList.get(bIndex);

            int cIndex = getNextUnmarkedPoint(bIndex + 1);
            c = contourPolygonList.get(cIndex);

            int resetIteratorIndex  = testPoints(a, b, c);


            if(resetIteratorIndex != -1) {
                listPositionIterator = this.contourPolygonList.listIterator(resetIteratorIndex);
            }
        }

        convexHullList = contourPolygonList;
        convexHullList.removeAll(removeList);

        return convexHullList;

    }

    private int getNextUnmarkedPoint(int startIndex) {
        int targetIndex = startIndex;

        ListIterator<Point> pointIterator = this.contourPolygonList.listIterator(targetIndex);

        while(pointIterator.hasNext()) {
            targetIndex = pointIterator.nextIndex();

            if (pointIterator.next().removeMarker == false) return targetIndex;
        }

        pointIterator = this.contourPolygonList.listIterator(0);

        while(pointIterator.hasNext()) {
            targetIndex = pointIterator.nextIndex();

            if (pointIterator.next().removeMarker == false) return targetIndex;
        }

        return targetIndex;
    }

    private int testPoints(Point a, Point b, Point c) {

        long ergebnis = calcWVT(a, c, b);
        if (ergebnis == 0) {
            System.out.println("Kollineraren Punkt entfernen");
            b.removeMarker = true;
            removeList.add(b);
            return(contourPolygonList.indexOf(a));

        }
        else if (ergebnis < 0) {
            ListIterator<Point> findEdgesConvexHullIterator = null;
            Point d, e;
            int startIndexRemove = this.contourPolygonList.indexOf(b);
            int endIndexRemove;
            int startIndex = this.contourPolygonList.indexOf(b);
            if(startIndex != 0) {
                findEdgesConvexHullIterator = this.contourPolygonList.listIterator(startIndex);
            }
            else {
                if(contourPolygonList.get(startIndex).isExtremePoint) {
                    endIndexRemove = startIndex;
                    removePoints(startIndexRemove, endIndexRemove);
                    return endIndexRemove;
                }
                else {
                    startIndex = contourPolygonList.size() -1;

                    if(contourPolygonList.get(startIndex).isExtremePoint) {
                        endIndexRemove = startIndex;
                        removePoints(startIndexRemove, endIndexRemove);
                        return endIndexRemove;
                    }
                    else {
                        findEdgesConvexHullIterator = this.contourPolygonList.listIterator(startIndex);
                    }
                }
            }

            while (findEdgesConvexHullIterator.hasPrevious() ) {
                d = findEdgesConvexHullIterator.previous();

                while(d.removeMarker == true) {
                    if(findEdgesConvexHullIterator.hasPrevious()) {
                        d = findEdgesConvexHullIterator.previous();
                    }
                    else {
                        findEdgesConvexHullIterator = this.contourPolygonList.listIterator(contourPolygonList.size() -1);
                    }
                }

                if(d.isExtremePoint) {
                    endIndexRemove = contourPolygonList.indexOf(d);
                    removePoints(startIndexRemove, endIndexRemove);
                    return endIndexRemove;
                }

                else if (findEdgesConvexHullIterator.hasPrevious()) {
                    e = findEdgesConvexHullIterator.previous();
                    while( e.removeMarker == true) {
                        if(findEdgesConvexHullIterator.hasPrevious()) {
                            e = findEdgesConvexHullIterator.previous();
                        }
                        else {
                            findEdgesConvexHullIterator = this.contourPolygonList.listIterator(contourPolygonList.size() - 1);
                        }
                    }

                    if(e.isExtremePoint) {
                        endIndexRemove = contourPolygonList.indexOf(d);
                        removePoints(startIndexRemove, endIndexRemove);
                        return endIndexRemove;
                    }
                    else {
                        long ergebnis2 = calcWVT(e, c, d);
                        if (ergebnis2 > 0) {
                            endIndexRemove = contourPolygonList.indexOf(d);
                            removePoints(startIndexRemove, endIndexRemove);
                            return endIndexRemove;
                        }
                        else {
                            findEdgesConvexHullIterator.next();
                        }
                    }
                }
            }
        }

        return -1;
    }

    private void removePoints(int startIndexRemove, int endIndexRemove) {
        // TODO Auto-generated method stub
        int hilfe = contourPolygonList.indexOf(contourPolygonList.getLast());
        while (startIndexRemove != endIndexRemove) {

            contourPolygonList.get(startIndexRemove).removeMarker = true;
            removeList.add(contourPolygonList.get(startIndexRemove));
            if (startIndexRemove == 0) {
                startIndexRemove = hilfe;
            } else {
                startIndexRemove--;
            }
        }
    }

    public String[] getContour() {
        int i = 0;
        String[] contour = new String[convexHullList.size()];
        for (Point p : convexHullList) {

            contour[i] = (p.x + "," + p.y);
            i++;
        }
        return contour;
    }
}
