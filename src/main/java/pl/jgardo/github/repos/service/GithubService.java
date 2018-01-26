package pl.jgardo.github.repos.service;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.jgardo.github.repos.client.GithubClient;
import pl.jgardo.github.repos.client.exception.NotFoundException;
import pl.jgardo.github.repos.client.exception.RepoNotFoundException;
import pl.jgardo.github.repos.client.model.Repository;
import rx.Single;

import java.util.Optional;


@Service
public class GithubService {

	private final GithubClient githubClient;

	@Autowired
	public GithubService(GithubClient githubClient) {
		this.githubClient = githubClient;
	}

	public Single<Repository> getRepo(String owner, String repo) {
		return doGetRepo(owner, repo)
				.onErrorResumeNext(exception -> handleGetRepoException(owner, repo, exception));
	}

	private Single<Repository> doGetRepo(String owner, String repo) {
		return validateGetRepoParams(owner, repo)
				.<Single<Repository>>map(Single::error)
				.orElseGet(() -> githubClient.retrieveRepository(owner, repo).toObservable().toSingle());
	}

	private Optional<IllegalArgumentException> validateGetRepoParams(String owner, String repo) {
		if (StringUtils.isBlank(owner)) {
			return Optional.of(new IllegalArgumentException("Owner must not be blank"));
		} else if (StringUtils.isBlank(repo)) {
			return Optional.of(new IllegalArgumentException("Repo must not be blank"));
		} else {
			return Optional.empty();
		}
	}

	private Single<Repository> handleGetRepoException(String owner, String repo, Throwable cause) {
		if (cause instanceof HystrixRuntimeException && cause.getCause() instanceof NotFoundException) {
			return Single.error(new RepoNotFoundException(owner, repo));
		} else if (cause instanceof IllegalArgumentException) {
			return Single.error(cause);
		} else {
			return Single.error(new RuntimeException(String.format("Unknown exception occurs for owner: %s repo: %s", owner, repo), cause));
		}
	}
}
