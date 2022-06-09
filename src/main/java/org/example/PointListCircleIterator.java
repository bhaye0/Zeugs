package org.example;

import java.util.LinkedList;
import java.util.ListIterator;

public class PointListCircleIterator {
    private LinkedList<Point> points;
    private ListIterator<Point> it;

    private Point currentPoint;
    private int currentIndex = 0;

    public PointListCircleIterator(LinkedList<Point> points) {
        this.points = points;
        this.it = points.listIterator();
    }

    public Point next() {
        if (!it.hasNext()) {
            it = points.listIterator();
        }

        currentIndex = it.nextIndex();
        currentPoint = it.next();

        return currentPoint;
    }

    public Point previous() {
        if (!it.hasPrevious()) {
            int lastIndex = points.size() -1 ;
            it = points.listIterator(lastIndex);
        }

        currentIndex = it.previousIndex();
        currentPoint = it.previous();

        return currentPoint;
    }

    public Point current() {
        return currentPoint;
    }

    public Boolean isLastElement() {
        return !it.hasNext();
    }

    public Boolean isFirstElement() {
        return !it.hasPrevious();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getPreviousIndex() {
        return (it.hasPrevious()) ? it.previousIndex() : (points.size() - 1);
    }

    public int getNextIndex() {
        return (it.hasNext()) ? it.nextIndex() : 0;
    }
}


