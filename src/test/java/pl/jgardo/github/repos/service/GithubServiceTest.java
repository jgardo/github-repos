package pl.jgardo.github.repos.service;

import java.time.ZonedDateTime;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import pl.jgardo.github.repos.client.GithubClient;
import pl.jgardo.github.repos.client.exception.HttpException;
import pl.jgardo.github.repos.client.exception.NotFoundException;
import pl.jgardo.github.repos.client.exception.RepoNotFoundException;
import pl.jgardo.github.repos.client.model.Repository;

public class GithubServiceTest {

	@Test(expected = IllegalArgumentException.class)
	public void testOwnerEmpty() {
//		given
		GithubService githubService = new GithubService(Mockito.mock(GithubClient.class));
		
//		when
		githubService.getRepo("", "notEmptyRepo");
		
//		then
//		exception expected
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRepoEmpty() {
//		given
		GithubService githubService = new GithubService(Mockito.mock(GithubClient.class));
		
//		when
		githubService.getRepo("notEmptyOwner", "");
		
//		then
//		exception expected
	}
	
	@Test
	public void testExistingRepo() {
//		given
		Repository repo = new Repository("name" , "desc", "cloneUrl", 1, ZonedDateTime.now());
		GithubClient githubClient = Mockito.mock(GithubClient.class);
		Mockito.when(githubClient.retrieveRepository(Mockito.any(), Mockito.any())).thenReturn(repo);

		GithubService githubService = new GithubService(githubClient);
//		when
		Repository result = githubService.getRepo("notEmptyOwner", "notEmptyRepo");
		
//		then
		Assert.assertSame(repo, result);
	}
	
	@Test(expected=RepoNotFoundException.class)
	public void testNotExistingRepo() {
//		given
		GithubClient githubClient = Mockito.mock(GithubClient.class);
		Mockito.when(githubClient.retrieveRepository(Mockito.any(), Mockito.any()))
		.thenThrow(new NotFoundException(""));

		GithubService githubService = new GithubService(githubClient);
//		when
		githubService.getRepo("notEmptyOwner", "notEmptyRepo");
		
//		then
//		exception expected
	}

	@Test(expected=RuntimeException.class)
	public void testFailure() {
//		given
		GithubClient githubClient = Mockito.mock(GithubClient.class);
		Mockito.when(githubClient.retrieveRepository(Mockito.any(), Mockito.any()))
		.thenThrow(new HttpException("", 500));

		GithubService githubService = new GithubService(githubClient);
//		when
		githubService.getRepo("notEmptyOwner", "notEmptyRepo");
		
//		then
//		exception expected
	}	
}
