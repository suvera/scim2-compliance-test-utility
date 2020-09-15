package dev.suvera.opensource.scim2.compliance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Scim2Application {

	public static void main(String[] args) {
		SpringApplication.run(Scim2Application.class, args);
	}

}
