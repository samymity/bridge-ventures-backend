package com.example.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Solution4 {

	
public static void main(String[] args) {
	
	
	List<Employee> employees = new ArrayList<>();
	
	employees.add(new Employee("A", 45));
	employees.add(new Employee("B", 22));
	employees.add(new Employee("C", 32));
	employees.add(new Employee("D", 32));
	
	
	
	
	List<Integer>  result = employees.stream().map(employee -> employee.getAge()).collect( Collectors.toList());
	
	Collections.sort(result);
	System.out.println(result);
	
	
	
}
	
	 
}
