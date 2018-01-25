package pl.jgardo.github.repos.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import pl.jgardo.github.repos.client.GithubClient;
import pl.jgardo.github.repos.client.exception.NotFoundException;
import pl.jgardo.github.repos.client.exception.RepoNotFoundException;
import pl.jgardo.github.repos.client.model.Repository;


@Service
public class GithubService {

	private final GithubClient githubClient;

	@Autowired
	public GithubService(GithubClient githubClient) {
		this.githubClient = githubClient;
	}

	public Repository getRepo(String owner, String repo) {
		try {
			return doGetRepo(owner, repo);
		} catch (Exception e) {
			return handleGetRepoException(owner, repo, e);
		}
	}

	private Repository doGetRepo(String owner, String repo) {
		validateGetRepoParams(owner, repo);

		return githubClient.retrieveRepository(owner, repo);
	}

	private void validateGetRepoParams(String owner, String repo) {
		Assert.isTrue(StringUtils.isNotBlank(owner), "Owner must not be blank");
		Assert.isTrue(StringUtils.isNotBlank(repo), "Repo must not be blank");
	}

	private Repository handleGetRepoException(String owner, String repo, Throwable cause) {
		if (cause instanceof NotFoundException) {
			throw new RepoNotFoundException(owner, repo);
		} else if (cause instanceof IllegalArgumentException) {
			throw (IllegalArgumentException) cause;
		} else {
			throw new RuntimeException(String.format("Unknown exception occurs for owner: %s repo: %s", owner, repo), cause);
		}
	}
}
