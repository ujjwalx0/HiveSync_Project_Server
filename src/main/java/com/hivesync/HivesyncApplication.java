package com.hivesync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"com.hivesync"})
public class HivesyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(HivesyncApplication.class, args);
	}

}
