package pl.jgardo.github.repos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@Import(FeignClientsConfiguration.class)
public class GithubReposApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubReposApplication.class, args);
	}
}
