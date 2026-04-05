package MaidRepository.maid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class MaidRepositoryApplication {
	public static void main(String[] args) {
		SpringApplication.run(MaidRepositoryApplication.class, args);
		System.out.println(" Application started successfully!");
		System.out.println(" Health Check: http://localhost:8081/api/health");
		System.out.println(" Ping Test: http://localhost:8081/api/ping");
	}
}