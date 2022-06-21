package org.example;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;

public class TypeCastPerformance {
    public static void main(String[] args) {
        ArrayList<Integer> values = buildRandIntArray(-10000, 10000, 5000);

        Function<ArrayList<Integer>, Integer> loopWithoutCast = (val) -> {
            for (int value : values) {
                int result = value + 1;
            }

            return 0;
        };

        Function<ArrayList<Integer>, Integer> loopWithCast = (val) -> {
            for (int value : values) {
                double result = (double)(int)((long)value + (long)1);
            }

            return 0;
        };

        measureRuntime(loopWithoutCast, values);
        measureRuntime(loopWithCast, values);
    }

    public static ArrayList<Integer> buildRandIntArray(int min, int max, int size) {
        Random rand = new Random();

        ArrayList<Integer> values = new ArrayList<Integer>();

        for (int i = 0; i < size; i++) {
            values.add(rand.nextInt((max - min) + 1) + min);
        }

        return values;
    }

    public static void measureRuntime(Function<ArrayList<Integer>, Integer> func, ArrayList<Integer> values) {
        long timeA = System.nanoTime();

        func.apply(values);

        long timeB = System.nanoTime();
        System.out.println("() -> Elapsed time: " + (timeB - timeA));
    }
}
