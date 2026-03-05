package com.example.demo;

import java.util.*;

public class ManualFactory {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Charlie", "Alice", "Bob");
        List<Number> numbers = Arrays.asList(1,2,3,4);
     
        List<Integer> numberss = new ArrayList<>();
        

        // The "7-page manual" (Boilerplate)
        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.compareTo(b);
            }
        });

        System.out.println("Manual Sort: " + numbers);
    }
}