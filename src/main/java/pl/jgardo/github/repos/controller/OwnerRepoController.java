package pl.jgardo.github.repos.controller;

import java.time.ZoneId;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.jgardo.github.repos.client.model.Repository;
import pl.jgardo.github.repos.controller.dto.RepositoryDTO;
import pl.jgardo.github.repos.controller.mapper.RepositoryMapper;
import pl.jgardo.github.repos.service.GithubService;
import pl.jgardo.github.repos.timezone.TimeZoneService;

@RestController
@RequestMapping("/repositories")
public class OwnerRepoController {

	public static final String TIME_ZONE_HEADER_NAME = "Time-Zone";

	private final RepositoryMapper repositoryMapper;
	private final GithubService githubService;
	private final TimeZoneService timezoneService;
	
	public OwnerRepoController(RepositoryMapper repositoryMapper, GithubService githubService,
			TimeZoneService timezoneService) {
		super();
		this.repositoryMapper = repositoryMapper;
		this.githubService = githubService;
		this.timezoneService = timezoneService;
	}

	@GetMapping(path="/{owner}/{repositoryName}")
	public RepositoryDTO getRepoDetails(@PathVariable String owner,
                                        @PathVariable String repositoryName,
                                        @RequestHeader(name=TIME_ZONE_HEADER_NAME,required = false) String timeZone){
				
		ZoneId zoneId = timezoneService.getZoneIdByTimeZoneId(timeZone);
		Repository repository = githubService.getRepo(owner, repositoryName);

		return repositoryMapper.convertToDtoUsingZoneId(repository, zoneId);
	}
}
