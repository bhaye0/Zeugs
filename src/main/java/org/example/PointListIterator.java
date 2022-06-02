package org.example;

import java.util.LinkedList;
import java.util.ListIterator;

public class PointListIterator {
    private LinkedList<PointAttributes> points;
    private ListIterator<PointAttributes> it;

    private PointAttributes currentPoint;
    private int currentIndex = 0;

    public PointListIterator(LinkedList<PointAttributes> points) {
        this.points = points;
        this.it = points.listIterator();
    }

    public PointAttributes getNextPoint() {
        if (it.hasNext()) {
            currentIndex = it.nextIndex();
            return it.next();
        }
        else {
            currentIndex = 0;

        }

        return (it.hasNext()) ? it.next(). : points.list
    }


}
