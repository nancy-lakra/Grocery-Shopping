package com.training.grocery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = "com.training.grocery")
public class GroceryApplication {
	public static void main(String[] args) {
		SpringApplication.run(GroceryApplication.class, args);
	}
}
