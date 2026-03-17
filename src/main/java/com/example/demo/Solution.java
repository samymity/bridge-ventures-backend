package com.example.demo;

import java.util.*;
import java.util.stream.*;

public class Solution {

	public static void main(String[] args) {

		List<Employee> empList = List.of(
			new Employee("Alice",   30),
			new Employee("Bob",     30),
			new Employee("Charlie", 35),
			new Employee("Anna",    28),
			new Employee("Bob",     40)
		);
		
		
		
		
		School sch = new School("A", 4);
		
		String   s  = "sandeep";
		s.toUpperCase();
		
		System.out.println(s);
				
	

		// 1. filter — employees older than 28
		System.out.println("--- filter: age > 28 ---");
		 empList.stream()
			.filter(e -> e.getAge() > 28)
			.forEach(e -> System.out.println(e.getName() + " " + e.getAge()));
		
		
		
		List<Employee> empList1 = empList.stream()
				.filter(e -> e.getAge() > 28)
				.collect(Collectors.toList());
		
		empList1.forEach(n ->  System.out.println(n.getAge()));
		


		// 2. map — extract names
		System.out.println("\n--- map: names ---");
		List<String> names = empList.stream()
			.map(Employee::getName)
			.collect(Collectors.toList());
		System.out.println(names);

		// 3. distinct — unique names
		System.out.println("\n--- distinct: unique names ---");
		empList.stream()
			.map(Employee::getName)
			.distinct()
			.forEach(System.out::println);

		// 4. sorted — by age ascending
		System.out.println("\n--- sorted: by age ---");
		empList.stream()
			.sorted(Comparator.comparingInt(Employee::getAge))
			.forEach(e -> System.out.println(e.getName() + " " + e.getAge()));

		// 5. count
		long count = empList.stream()
			.filter(e -> e.getAge() >= 30)
			.count();
		System.out.println("\n--- count: age >= 30 --- " + count);

		// 6. min / max
		empList.stream()
			.min(Comparator.comparingInt(Employee::getAge))
			.ifPresent(e -> System.out.println("\n--- min age --- " + e.getName() + " " + e.getAge()));

		empList.stream()
			.max(Comparator.comparingInt(Employee::getAge))
			.ifPresent(e -> System.out.println("--- max age --- " + e.getName() + " " + e.getAge()));

		// 7. anyMatch / allMatch / noneMatch
		System.out.println("\n--- match ---");
		System.out.println("anyMatch age > 38 : " + empList.stream().anyMatch(e -> e.getAge() > 38));
		System.out.println("allMatch age > 20 : " + empList.stream().allMatch(e -> e.getAge() > 20));
		System.out.println("noneMatch age < 0 : " + empList.stream().noneMatch(e -> e.getAge() < 0));

		// 8. reduce — sum of ages
		int totalAge = empList.stream()
			.mapToInt(Employee::getAge)
			.reduce(0, Integer::sum);
		System.out.println("\n--- reduce: total age --- " + totalAge);

		// 9. mapToInt — average age
		OptionalDouble avgAge = empList.stream()
			.mapToInt(Employee::getAge)
			.average();
		avgAge.ifPresent(a -> System.out.println("--- average age --- " + a));

		// 10. groupingBy — group by name
		System.out.println("\n--- groupingBy: name ---");
		Map<String, List<Employee>> grouped = empList.stream()
			.collect(Collectors.groupingBy(Employee::getName));
		grouped.forEach((name2, list) ->
			System.out.println(name2 + " -> " + list.stream().map(Employee::getAge).collect(Collectors.toList())));

		// 11. joining — names as comma-separated string
		System.out.println("\n--- joining ---");
		String joined = empList.stream()
			.map(Employee::getName)
			.distinct()
			.collect(Collectors.joining(", ", "[", "]"));
		System.out.println(joined);

		// 12. partitioningBy — age >= 30 or not
		System.out.println("\n--- partitioningBy: age >= 30 ---");
		Map<Boolean, List<Employee>> partitioned = empList.stream()
			.collect(Collectors.partitioningBy(e -> e.getAge() >= 30));
		partitioned.forEach((senior, list) ->
			System.out.println(senior + " -> " + list.stream().map(Employee::getName).collect(Collectors.toList())));

		// 13. flatMap — simulate nested lists
		System.out.println("\n--- flatMap ---");
		List<List<String>> nested = List.of(
			List.of("Alice", "Bob"),
			List.of("Charlie", "Anna")
		);
		nested.stream()
			.flatMap(Collection::stream)
			.forEach(System.out::println);

		// 14. findFirst
		System.out.println("\n--- findFirst: name starts with 'C' ---");
		empList.stream()
			.filter(e -> e.getName().startsWith("C"))
			.findFirst()
			.ifPresent(e -> System.out.println(e.getName()));

		// 15. limit / skip
		System.out.println("\n--- limit(3) / skip(1) ---");
		empList.stream()
			.skip(1)
			.limit(3)
			.forEach(e -> System.out.println(e.getName()));
	}
}
