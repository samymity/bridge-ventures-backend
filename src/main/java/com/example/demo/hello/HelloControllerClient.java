package com.example.demo.hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
public class HelloControllerClient {

    // Spring automatically injects the HelloService bean here
    @Autowired
    private HelloService helloService;

    // Endpoint 1: GET /api/hello
    @GetMapping(("/start"))
    public String hello(@RequestParam(defaultValue = "World") String name) {
        return helloService.greet(name);
    }

    // Endpoint 2: GET /api/hello/bye
    @GetMapping("/bye")
    public String bye(@RequestParam(defaultValue = "World") String name) {
        return helloService.farewell(name);
    }
}
