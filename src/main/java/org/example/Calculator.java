package propra22.q5371210.CHGO.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.example.ConvexHullCalculator;
import org.example.DiameterCalculator;
import org.example.PointPair;
import org.example.Point;
import propra22.interfaces.IHullCalculator;

public class Calculator implements IHullCalculator {

    private SetOfPoints setOfPoints;
    LinkedList<Point> contourPolygonList;
    LinkedList<Point> convexHullList;
    LinkedList<Point> removeList ;
    int[][] convexHull;

    public Calculator() {
        this.setOfPoints = new SetOfPoints();
        this.contourPolygonList = new LinkedList<Point>();
        this.convexHullList = new LinkedList<Point>();
        this.removeList = new LinkedList<Point>();
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
        this.contourPolygonList = new LinkedList<Point>();
        this.convexHullList = new LinkedList<Point>();
        this.removeList = new LinkedList<Point>();
    }

    @Override
    public int[][] getConvexHull() {
        int[][] convexHull = ConvexHullCalculator.getConvexHull();
        return convexHull;
    }

    @Override
    public int[][] getDiameter() {
        PointPair diameter = DiameterCalculator.getDiameter(convexHullList);
        return diameter.toArray();
    }
}