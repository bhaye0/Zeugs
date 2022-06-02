
Suchen



        Machen wir
        Bildanhang
        Bildanhang
        Das sieht ja gut aus 👍
        Ja, hab selten so lecker gegessen
        Guten Morgen. Geht es euch gut?😘
        Ja und dir 😘
        Ja, bei uns ist alles prima
        Wir fahren gleich nach Weimar
        Dann viel Spaß 😘
        Danke. Euch auch😘
        Bildanhang
        Bildanhang
        Bildanhang
        Bildanhang
        Bildanhang
        Bildanhang
        Bildanhang
        Sehr schön.
        Ja... Wobei Weimar ansonsten jetzt nicht so toll ist. Außer einer Stadtführung kann man da nicht allzu viel machen
        Achso, wolltest du gleich nochmal mit den Kindern sprechen?
        Melde mich gleich
        Geht's euch gut?😘
        Ja und dir? 😘
        Auch. Wir nähern uns der Heimat. War macht ihr Schönes?
        Gerade ein bisschen ausruhen.
        Wir waren heute früh auf dem Trödelmarkt und bei meiner Mutter.
        Wann kommt ihr denn ungefähr an?
        Bin denke ich mal gegen 7 Zuhause
        OK 😘
        Bin kurz nach 7 da
        https://www.danisch.de/blog/2022/05/29/big-trouble-in-little-flensburg/
        package propra22.q5371210.CHGO.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.example.PointAttributes;
import org.example.PointTriple;
import propra22.interfaces.IHullCalculator;

public class Calculator implements IHullCalculator {

    private SetOfPoints setOfPoints;
    LinkedList<PointAttributes> contourPolygonList;
    LinkedList<PointAttributes> convexHullList;
    LinkedList<PointAttributes> removeList ;

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

        convexHullList = (contourPolygonList.size() <= 2) ?
            contourPolygonList :
            rekursiveBerechnung(contourPolygonList);

        int[][] convexHull = new int[convexHullList.size()][2];

        for(int i = 0; i < convexHullList.size(); i++) {
            convexHull[i][0] = convexHullList.get(i).x;
            convexHull[i][1] = convexHullList.get(i).y;
        }

        return convexHull;
    }

    private LinkedList<PointAttributes> rekursiveBerechnung(LinkedList<PointAttributes> contourPolygonList) {
        ListIterator<PointAttributes> listPositionIterator = this.contourPolygonList.listIterator();

        while (listPositionIterator.hasNext()) {
            int currentIndex = listPositionIterator.nextIndex();

            PointAttributes currentPoint = listPositionIterator.next();

            if(currentPoint.removeMarker == false) {
                PointTriple pTriple = getPointTriple(contourPolygonList, currentIndex);
                testPoints(pTriple.a, pTriple.b, pTriple.c);
            }
        }

        convexHullList = contourPolygonList;
        convexHullList.removeAll(removeList);
        return convexHullList;
    }

    private PointTriple getPointTriple(LinkedList<PointAttributes> contourPolygonList, int currentIndex) {
        PointTriple pTriple = new PointTriple();

        pTriple.a = contourPolygonList.get(currentIndex);

        pTriple.b = (currentIndex + 1 < contourPolygonList.size()) ?
            contourPolygonList.get(currentIndex + 1) :
            contourPolygonList.getFirst();

        if (currentIndex + 2 < contourPolygonList.size()) {
            pTriple.c = contourPolygonList.get(currentIndex + 2);
        }
        else if (currentIndex + 1 < contourPolygonList.size()) {
            pTriple.c = contourPolygonList.getFirst();
        }
        else {
            pTriple.c = contourPolygonList.get(1);
        }
        return pTriple;
    }

    private void testPoints(PointAttributes a, PointAttributes b, PointAttributes c) {
        long ergebnis = ((a.x) * (b.y - c.y)) + ((b.x) * (c.y - a.y)) + ((c.x) * (a.y - b.y));

        if (ergebnis == 0) {
            System.out.println("Kollineraren Punkt entfernen");
            b.removeMarker = true;
            removeList.add(b);
        }
        if (ergebnis < 0) {
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
                }
                else {
                    startIndex = contourPolygonList.indexOf(contourPolygonList.getLast());
                    if(contourPolygonList.get(startIndex).isExtremePoint) {
                        endIndexRemove = startIndex;
                        removePoints(startIndexRemove, endIndexRemove);
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
                        int indexOfLastElement = contourPolygonList.indexOf(contourPolygonList.getLast());
                        findEdgesConvexHullIterator = this.contourPolygonList.listIterator(indexOfLastElement);
                    }
                }

                if(d.isExtremePoint) {
                    endIndexRemove = contourPolygonList.indexOf(d);
                    removePoints(startIndexRemove, endIndexRemove);
                }
                else if (findEdgesConvexHullIterator.hasPrevious()) {
                    e = findEdgesConvexHullIterator.previous();
                    while( e.removeMarker == true) {
                        if(findEdgesConvexHullIterator.hasPrevious()) {
                            e = findEdgesConvexHullIterator.previous();
                        }
                        else {
                            findEdgesConvexHullIterator = this.contourPolygonList.listIterator(contourPolygonList.indexOf(contourPolygonList.getLast()));
                        }
                    }

                    if(e.isExtremePoint) {
                        endIndexRemove = contourPolygonList.indexOf(d);
                        removePoints(startIndexRemove, endIndexRemove);
                    }
                    else {
                        long ergebnis2 = ((e.x) * (d.y - c.y)) + ((d.x) * (c.y - e.y)) + ((c.x) * (e.y - d.y));

                        if (ergebnis2 > 0) {
                            endIndexRemove = contourPolygonList.indexOf(d);
                            removePoints(startIndexRemove, endIndexRemove);
                        }
                        else {
                            findEdgesConvexHullIterator.next();
                        }
                    }
                }
            }
        }
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getDiameterLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getEmail() {
        // TODO Auto-generated method stub
        String eMail = "laura-pannier@t-online.de";

        return eMail;
    }

    @Override
    public String getMatrNr() {
        // TODO Auto-generated method stub
        String matrNr = "5371210";

        return matrNr;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        String name = "Laura Pannier";

        return name;
    }

    @Override
    public int[][] getQuadrangle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getQuadrangleArea() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int[][] getTriangle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getTriangleArea() {
        // TODO Auto-generated method stub
        return 0;
    }

}






