package org.example;

import java.util.*;
import java.util.function.Function;

public class HashSetTest {
    public static void main(String[] args) {
        String a = "123";
        String b = "234";
        String c = "123";

        HashSet<String> hs = new HashSet<String>();

        hs.add(a);

        System.out.println(hs.contains(c));
        hs.add(b);
        hs.add(c);

        System.out.println(hs.size());
        System.out.println(hs.contains("123"));

        List<Integer> myList1 = new LinkedList<>();
        myList1 = new ArrayList<Integer>(myList1);
        myList1 = new LinkedList<Integer>(myList1);
    }
}
