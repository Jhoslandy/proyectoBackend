package com.example.ProyectoTaw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class}) // <-- Añade el exclude
//@EnableCaching // Si estás usando caché
public class ProyectoTawApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoTawApplication.class, args);
	}

}
