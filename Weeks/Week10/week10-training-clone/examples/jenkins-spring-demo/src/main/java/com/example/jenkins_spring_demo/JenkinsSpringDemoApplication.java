package com.example.jenkins_spring_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class JenkinsSpringDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JenkinsSpringDemoApplication.class, args);
	}

	@GetMapping("/")
	public String hello(){
		return "Hello from Jenkins Deploy";
	}

}
