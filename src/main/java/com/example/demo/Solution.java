package com.example.demo;

import java.util.*;
import java.util.stream.*;

public class Solution {

	public static void main(String[] args) {


		System.out.println("test");
		
		
		Addition1 add = new Addition1();
		
		
		System.out.println(add.add(2, 3));
		
		
		
		Addition2 add2 = (a,b) -> a-b;
		
		
		int result =  add2.add(4, 5);
		

		Addition2 add3 = (a,b) -> a*b;
		
		
		int result2 =  add3.add(4, 5);
}
	
}
