package com.srizvi.spring;

import com.srizvi.spring.domain.Role;
import com.srizvi.spring.domain.User;
import com.srizvi.spring.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class SpringSecurityAppApplication {



	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityAppApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}


	@Bean
	CommandLineRunner run(UserService userService){
       return args -> {

		   userService.saveRole(new Role(null, "ROLE_USER"));
		   userService.saveRole(new Role(null, "ROLE_MANAGER"));
		   userService.saveRole(new Role(null, "ROLE_ADMIN"));
		   userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

		   userService.saveUser(new User(null, "Jhon Travolata", "Jhon", "1234", new ArrayList<>()));
		   userService.saveUser(new User(null, "Jim Carey", "Jim", "5678", new ArrayList<>()));
		   userService.saveUser(new User(null, "Harry Smith", "Harry", "1111", new ArrayList<>()));
		   userService.saveUser(new User(null, "Syed Rizvi", "Syed", "2222", new ArrayList<>()));

		   userService.addRoleToUser("Jhon","ROLE_USER");
		   userService.addRoleToUser("Jim","ROLE_MANAGER");
		   userService.addRoleToUser("Harry","ROLE_ADMIN");
		   userService.addRoleToUser("Syed","ROLE_SUPER_ADMIN");
		   userService.addRoleToUser("Jhon","ROLE_SUPER_ADMIN");
		   userService.addRoleToUser("Harry","ROLE_SUPER_ADMIN");
		   userService.addRoleToUser("Syed","ROLE_USER");

	   };
	}

}


