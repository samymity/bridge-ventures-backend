package com.example.demo;
import java.util.*;
import java.util.stream.Collectors;

public class SmartFactory {
    public static void main(String[] args) {
        List<String> warehouse = Arrays.asList("alice", "BOB", "charlie", "david");

        List<String> result = warehouse.stream()
            .filter(n -> n.startsWith("a"))      // Robot 1: Keep only 'a'
            .map(String::toUpperCase)            // Robot 2: Transform to Uppercase
            .collect(Collectors.toList());       // Packaging: Put back in a List

        System.out.println("Processed Items: " + result); // Output: [ALICE]
    }
}