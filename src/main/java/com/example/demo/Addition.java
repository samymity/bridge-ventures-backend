package com.example.demo;

import java.util.HashMap;
import java.util.Map;

public class Addition {
	
	
	
	
	
	
	public  int[] twoSum ( int nums[] , int target)
	{
	
		Map<Integer,Integer> map = new HashMap<>();
		for (int i=0; i<nums.length; i++) {
		  int comp = target - nums[i];
		  if (map.containsKey(comp))
		    return new int[]{map.get(comp),i};
		  map.put(nums[i], i);
		}
		
		return new int[0] ;
		
	}
	

	public  void twoSum1( )
	{
	
		Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		map.get("a");                           // 1
		map.getOrDefault("x", 0);             // 0 if absent
		map.containsKey("a");                  // true
		map.put("a", map.getOrDefault("a",0)+1); // frequency count pattern

		
	}

	
	public  int[] twoSum2( )
	{
	
		int[] nums = { 2 ,3 , 7 , 8};
		int target = 10;
		
		Map< Integer, Integer > map = new  HashMap<>();
		
		for ( int i =0 ; i< nums.length ; i++)
		{
			int compliment = target - nums[i];
			
			if ( map.containsKey(compliment))
			{
			  return new int[] { map.get(compliment),i };
			}
			else 
			{
				map.put(nums[i], i);
			}
		}
		
		
		System.out.println(map);
		return  new int[0];
		
	}
	
	
	








   public int[] twoSum3(int[] nums , int target)
   {
	   
	   Map<Integer , Integer > map = new HashMap<>();
	  
	   for (int i = 0; i < nums.length; i++) {
		   
		 int  compliment = target - nums[i] ; 
		 
		 if ( map.containsKey(compliment))
			 
		 {
			 
			 return new int[] { map.get(compliment) , i};
		 }
		 
		 else
		 {
		   map.put(nums[i], i);
		 }
		
	}
	   
	   return new int[0];
	   
   }
   



}
