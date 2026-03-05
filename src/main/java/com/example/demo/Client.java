package com.example.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		f1();
		
	 f2();


		
		
		f4();
		f5();
		
		
	}
	
	
	private static void f4() {
		// TODO Auto-generated method stub
		
		MyInterface inter = a -> a*a  ;
		
		System.out.println(inter.speak(4));
		
	}


	


	static void f1()
	{
		
		List<String> names = Arrays.asList( "Alics" , "BoB" , "Charlie" );
		
		List<String> results = names.stream()

                .filter(s -> s.startsWith("A")) 

                .collect(Collectors.toList());
		
		  System.out.println(results);
		
	}
	
	
	
	static void f2()
	{
		
		MathOperation mo =  (a , b) -> a+b ;
		
	     System.out.println(mo.readTask(2, 3));
		
	}
	
	

	static void f5()
	{
		
		List<Employee> employees = new ArrayList<>();
		employees.add(new Employee("sandeep" , 43));
		employees.add(new Employee("Honey" , 42));
		employees.add(new Employee("pradeep" , 47));
		
		
		// highest/lowest
		Optional<Employee> oldest = employees.stream().max(Comparator.comparing(emp -> emp.name));
		
		 //sort 
		
		employees.sort(Comparator.comparing(emp -> emp.name));
		
		//print
		employees.stream().forEach( a ->   System.out.println( "Name " + a.name) );
		   
		   
		   oldest.ifPresent(emp -> System.out.println(emp.name));
		
	}
	
	
	
	
	
	

}
