package pl.jgardo.github.repos.client;

import org.apache.http.HttpStatus;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import feign.Response;
import feign.codec.ErrorDecoder;
import pl.jgardo.github.repos.client.exception.HttpException;
import pl.jgardo.github.repos.client.exception.NotFoundException;
import pl.jgardo.github.repos.client.model.Repository;

@FeignClient(name="githubClient", url="${github.rest.url}", configuration=GithubClientConfiguration.class)
public interface GithubClient {
	@RequestMapping(value = "/repos/{owner}/{repo}", method = RequestMethod.GET)
	Repository retrieveRepository(@PathVariable("owner") String owner, @PathVariable("repo") String repo);
}

@Configuration
class GithubClientConfiguration {
	
	@Bean
	public ErrorDecoder errorDecoder() {
		return new GithubClientErrorDecoder();
	}
}

class GithubClientErrorDecoder implements ErrorDecoder {
	@Override
	public Exception decode(String methodKey, Response response) {
		String url = response.request().url();
		int status = response.status();
		if (status == HttpStatus.SC_NOT_FOUND) {
			return new NotFoundException(url);
		}
		
		return new HttpException(url, status);
	}	
}