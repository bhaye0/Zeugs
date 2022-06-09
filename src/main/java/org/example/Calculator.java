package propra22.q5371210.CHGO.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.example.PointPair;
import org.example.PointAttributes;
import propra22.interfaces.IHullCalculator;

public class Calculator implements IHullCalculator {

    private SetOfPoints setOfPoints;
    LinkedList<PointAttributes> contourPolygonList;
    LinkedList<PointAttributes> convexHullList;
    LinkedList<PointAttributes> removeList ;
    int[][] convexHull;

    public Calculator() {
        this.setOfPoints = new SetOfPoints();
        this.contourPolygonList = new LinkedList<PointAttributes>();
        this.convexHullList = new LinkedList<PointAttributes>();
        this.removeList = new LinkedList<PointAttributes>();
    }

    public String[] getPoints() {
        return setOfPoints.getList();
    }

    @Override
    public void addPoint(int x, int y) {
        String point = x + "," + y;

        if (setOfPoints.pointList != null) {
            for (String s : setOfPoints.getList()) {

                if (s.equals(point)) {
                    System.out.println("Fehler : Versuch Punkt hinzuzufügen, der sich schon in der Punktmenge befindet. Der Punkt wird nicht hinzugefügt.");
                    return;
                }
            }
        }
        setOfPoints.addPoint(point);

    }

    @Override
    public void addPointsFromArray(int[][] arg0) {
        // TODO Auto-generated method stub

        for (int i = 0; i <= arg0.length-1; i++) {
            this.addPoint(arg0[i][0], arg0[i][1]);
        }
        getConvexHull();
    }

    @Override
    public void addPointsFromFile(String filename) throws IOException {
        // TODO Auto-generated method stub
        FileReader fr = new FileReader(filename);
        try (BufferedReader br = new BufferedReader(fr)) {
            String line;

            while ((line = br.readLine()) != null) {
                Pattern pointSequence = Pattern.compile("^ *([-\\+]?\\d+) +([-\\+]?\\d+)(( .*)|( *))$");
                Matcher m = pointSequence.matcher(line);

                if (m.matches() == true) {
                    String stringXCoordinate = m.group(1);
                    int xCoordinate = Integer.valueOf(stringXCoordinate);
                    String stringYCoordinate = m.group(2);
                    int yCoordinate = Integer.valueOf(stringYCoordinate);
                    this.addPoint(xCoordinate, yCoordinate);

                }
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        getConvexHull();

    }

    @Override
    public void clearPoints() {
        // TODO Auto-generated method stub
        this.setOfPoints = new SetOfPoints();
        this.contourPolygonList = new LinkedList<PointAttributes>();
        this.convexHullList = new LinkedList<PointAttributes>();
        this.removeList = new LinkedList<PointAttributes>();
    }


    @Override
    public int[][] getConvexHull() {

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


        for(PointAttributes p : convexHullList) {
            convexHull[i][0] = p.x;
            convexHull[i][1] = p.y;
            i++;

        }

        return convexHull;

    }

    private LinkedList<PointAttributes> rekursiveBerechnung(LinkedList<PointAttributes> contourPolygonList) {
        ListIterator<PointAttributes> listPositionIterator = this.contourPolygonList.listIterator();
        PointAttributes a, b, c;

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

        ListIterator<PointAttributes> pointIterator = this.contourPolygonList.listIterator(targetIndex);

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

    private int testPoints(PointAttributes a, PointAttributes b, PointAttributes c) {

        long ergebnis = calcWVT(a, c, b);
        if (ergebnis == 0) {
            System.out.println("Kollineraren Punkt entfernen");
            b.removeMarker = true;
            removeList.add(b);
            return(contourPolygonList.indexOf(a));

        }
        else if (ergebnis < 0) {
            ListIterator<PointAttributes> findEdgesConvexHullIterator = null;
            PointAttributes d, e;
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

                while( d.removeMarker == true) {
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
        for (PointAttributes p : convexHullList) {

            contour[i] = (p.x + "," + p.y);
            i++;
        }
        return contour;
    }

    @Override
    public int[][] getDiameter() {
        //getBiggestDiameter();
        int size = convexHullList.size();
        PointPair pointPair;

        switch(size) {
            case 0: return new int[2][2];
            case 1:
            case 2: pointPair = getSimplePointDiameter(size); break;
            default: pointPair = getComplexDiameter();
        }

        pointPair.printToConsole();
        return pointPair.getDiameterArray();
    }

    private PointPair getSimplePointDiameter(int size) {
        PointPair pointPair = new PointPair();
        pointPair.pMin = convexHullList.getFirst();
        pointPair.pMin = (size == 1) ? convexHullList.getFirst() : convexHullList.getLast();
        return pointPair;
    }

    private PointPair getComplexDiameter() {
        PointPair resultPointPair = new PointPair();
        PointPair startPointPair = getStartPointPair();
        PointPair currentPointPair = startPointPair.copy();

        int indexOfpMin = convexHullList.indexOf(startPointPair.pMin);
        int indexOfpMax = convexHullList.indexOf(startPointPair.pMax);

        ListIterator<PointAttributes> pointIteratorFromMin = this.convexHullList.listIterator(indexOfpMin);
        ListIterator<PointAttributes> pointIteratorFromMax = this.convexHullList.listIterator(indexOfpMax);

        boolean setMinNext = false;
        boolean setMaxNext = false;

        while(currentPointPair.pMax != startPointPair.pMin && currentPointPair.pMin != startPointPair.pMax) {
            int indexCurrentA = (pointIteratorFromMax.hasNext()) ? pointIteratorFromMax.nextIndex() : 0;
            PointAttributes a = convexHullList.get(indexCurrentA);
            PointAttributes nachA = (indexCurrentA + 1 < convexHullList.size()) ?
                    convexHullList.get(indexCurrentA + 1) : convexHullList.get(0);

            int indexCurrentB = (pointIteratorFromMin.hasNext()) ? pointIteratorFromMin.nextIndex() : 0;
            PointAttributes b = convexHullList.get(indexCurrentB);
            PointAttributes nachB = (indexCurrentB + 1 < convexHullList.size()) ?
                    convexHullList.get(indexCurrentB + 1) : convexHullList.get(0);

            int x = b.x + a.x - nachB.x;
            int y = b.y + a.y - nachB.y;
            PointAttributes c = new PointAttributes(x,y);

            // a, nachA, c

            int wvt = calcWVT(a, c, nachA);

            if(wvt > 0) {
                wvtBiggerZero(pointIteratorFromMax, currentPointPair);
            }
            else if(wvt < 0) {
                wvtSmallerZero(pointIteratorFromMin, currentPointPair);
            }
            else if(wvt == 0) {
                long lengthDiameterFirstPair = ((a.x - nachA.x) * (a.x - nachA.x)) + ((a.y - nachA.y) * (a.y - nachA.y));
                long lengthDiameterSecondPair = ((b.x - nachB.x) * (b.x - nachB.x)) + ((startPointPair.pMax.y - nachB.y) * (startPointPair.pMax.y - nachB.y));

                if(lengthDiameterFirstPair < lengthDiameterSecondPair) {
                    if(!pointIteratorFromMax.hasNext()) {
                        pointIteratorFromMax = this.convexHullList.listIterator(convexHullList.indexOf(currentPointPair.pMax));
                    }

                    setMinNext = true;
                    currentPointPair.pMax = pointIteratorFromMax.next();
                }
                else{
                    if(!pointIteratorFromMin.hasNext()) {
                        pointIteratorFromMin = this.convexHullList.listIterator(convexHullList.indexOf(currentPointPair.pMin));
                    }

                    setMaxNext = true;
                    currentPointPair.pMin = pointIteratorFromMin.next();
                }
            }

            currentPointPair = getBiggerDiameter(resultPointPair, currentPointPair);

            if(setMinNext) {
                setMinNext = false;
                wvtSmallerZero(pointIteratorFromMin, currentPointPair);

                currentPointPair = getBiggerDiameter(resultPointPair, currentPointPair);
            }

            if(setMaxNext) {
                if(!pointIteratorFromMax.hasNext()) {
                    pointIteratorFromMax = this.convexHullList.listIterator(convexHullList.indexOf(currentPointPair.pMax));
                }

                setMaxNext = false;
                currentPointPair.pMax = pointIteratorFromMax.next();
                currentPointPair = getBiggerDiameter(resultPointPair, currentPointPair);
            }
        }

        while (currentPointPair.pMax == startPointPair.pMin && currentPointPair.pMin != startPointPair.pMax) {
            if(!pointIteratorFromMin.hasNext()) {
                pointIteratorFromMin = this.convexHullList.listIterator(convexHullList.indexOf(currentPointPair.pMin));
            }

            currentPointPair.pMin = pointIteratorFromMin.next();
            resultPointPair = getBiggerDiameter(resultPointPair, currentPointPair);
        }

        while (currentPointPair.pMax != startPointPair.pMin && currentPointPair.pMin == startPointPair.pMax) {
            if(!pointIteratorFromMax.hasNext()) {
                pointIteratorFromMax = this.convexHullList.listIterator(convexHullList.indexOf(currentPointPair.pMax));
            }

            currentPointPair.pMax = pointIteratorFromMax.next();
            resultPointPair = getBiggerDiameter(resultPointPair, currentPointPair);
        }

        return resultPointPair;
    }

    private PointPair getStartPointPair() {
        PointPair startPointPair = new PointPair();


        startPointPair.pMin = convexHullList.getFirst();
        startPointPair.pMax = convexHullList.getFirst();

        for(PointAttributes point : convexHullList) {
            if(point.comparedTo(startPointPair.pMax) == 1) startPointPair.pMax = point;
            if(point.comparedTo(startPointPair.pMin) == -1) startPointPair.pMin = point;
        }
        return startPointPair;
    }

    private void wvtSmallerZero(ListIterator<PointAttributes> pointIteratorFromMin, PointPair currentPointPair) {
        if(pointIteratorFromMin.hasNext()) {
            if(pointIteratorFromMin.nextIndex() == 0) {
                pointIteratorFromMin.next();
            }
            currentPointPair.pMin = pointIteratorFromMin.next();
        }
        else {
            currentPointPair.pMin = convexHullList.get(0);
            pointIteratorFromMin = this.convexHullList.listIterator(convexHullList.indexOf(currentPointPair.pMin));
        }
    }

    private void wvtBiggerZero(ListIterator<PointAttributes> pointIteratorFromMax, PointPair currentPointPair) {
        if(pointIteratorFromMax.hasNext()) {
            currentPointPair.pMax = pointIteratorFromMax.next();
        }
        else {
            currentPointPair.pMax = convexHullList.get(0);
            int pMaxIndex = convexHullList.indexOf(currentPointPair.pMax);
            pointIteratorFromMax = this.convexHullList.listIterator(pMaxIndex);
        }
    }

    private PointPair getBiggerDiameter(PointPair resultPointPair, PointPair currentPointPair) {
        int resultDistance = resultPointPair.calcDistanceSquared();
        int currentDistance = currentPointPair.calcDistanceSquared();

        return (currentDistance > resultDistance) ? currentPointPair.copy() : resultPointPair;
    }

    private int calcWVT(PointAttributes a, PointAttributes c, PointAttributes nachA) {
        return ((a.x) * (nachA.y - c.y)) + ((nachA.x) * (c.y - a.y)) + ((c.x) * (a.y - nachA.y));
    }

    public void getMyDiameter() {
        long myDiameter = 0;
        PointAttributes p1 = null;
        PointAttributes p2 = null;

        for (int i = 0; i < convexHullList.size(); i++) {
            for (int j = i + 1; j < convexHullList.size(); j++) {
                long deltaX = (convexHullList.get(i).x - convexHullList.get(j).x);
                long deltaY = (convexHullList.get(i).y - convexHullList.get(j).y);

                long thisDiameter = (deltaX * deltaX) + (deltaY * deltaY);

                if (thisDiameter > myDiameter) {
                    myDiameter = thisDiameter;
                    p1 = convexHullList.get(i);
                    p2 = convexHullList.get(j);
                }
            }
        }

        System.out.println("getBiggestDiameter() -> " + myDiameter + " at " + "[" + p1.x + ";" + p1.y  + "]," + "[" + p2.x + ";" + p2.y  + "]");
    }
}