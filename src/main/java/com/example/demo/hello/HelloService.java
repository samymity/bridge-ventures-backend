package com.example.demo.hello;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String greet(String name) {
        return "Hello, " + name + "! Welcome to Spring Boot.";
    }

    public String farewell(String name) {
        return "Goodbye, " + name + "! See you next time.";
    }
}
