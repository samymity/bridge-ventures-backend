package com.example.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		//f1();
		
	// f2();


		
		
	//	f4();
		f6();
		
		
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
	
	

		
	
	
	
	static void f6()
	{
		Addition addi = new Addition();
		/*
		 * int[] num = new int[] {1,2,3,4}; int target =6;
		 * 
		 * System.out.println( Arrays.toString(addi.twoSum(num, target)));
		 */
		int[] nums = {1,2,3,4};
		int target =6;
		
		System.out.println( Arrays.toString(addi.twoSum3(nums, target)));
	}
	
	

}