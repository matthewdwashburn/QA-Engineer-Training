package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@SpringBootApplication
@RestController
@CrossOrigin(origins = {"http://54.234.151.232:4173", "http://54.234.151.232:5173"})
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@GetMapping("/api/hello")
	public String hello(){
		return "Hello: " + LocalDateTime.now();
	}

	@GetMapping("/api/health")
	public String health(){
		return "Backend is healthy";
	}

}
