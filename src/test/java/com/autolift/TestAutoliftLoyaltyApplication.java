package com.autolift;

import org.springframework.boot.SpringApplication;

public class TestAutoliftLoyaltyApplication {

	public static void main(String[] args) {
		SpringApplication.from(AutoliftLoyaltyApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
