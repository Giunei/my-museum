package com.giunei.my_museum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MyMuseumApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyMuseumApplication.class, args);
	}

}
