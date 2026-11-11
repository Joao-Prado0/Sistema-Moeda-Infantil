package com.example.moeda_estudantil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MoedaEstudantilApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoedaEstudantilApplication.class, args);
	}

}
