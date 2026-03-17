package com.example.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution1 {
	
	
	
	// Assembly line 1
	static void f1(){
	
		// warehouse 
	List<String> str = new ArrayList<String>();
	str.add("sandeep");
	str.add("pradeep");
	str.add("preeti");
	str.add("qwer");
	str.add("preeti");
	
	
	//  Assembly line with tools ( map filter reduce , collect)
	
	
	
	
	Map<String , Long> map = str.stream().collect( Collectors.groupingBy(  s->s , Collectors.counting()));
	
	
	System.out.println(map);
	
	
	
	}
	
	
	
	public static void main(String[] args) {
		
		f1();
		
	}

}
