package business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FirstdockerappApplication {

	public static void main(String[] args) {
		SpringApplication.run(FirstdockerappApplication.class, args);
		
		System.out.println("Api is on");
	}

}
