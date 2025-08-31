package com.example.committeeportal.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
public class CommitteeController {
        @GetMapping("/hello")
        public String hello(){
            return "Hello, Committees!";
        }
}
