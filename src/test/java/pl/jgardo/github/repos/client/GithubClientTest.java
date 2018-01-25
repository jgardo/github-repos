package pl.jgardo.github.repos.client;

import static java.util.Collections.emptyMap;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cloud.netflix.feign.support.SpringMvcContract;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.netflix.hystrix.exception.HystrixRuntimeException;

import feign.Response;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.mock.HttpMethod;
import feign.mock.MockClient;
import feign.mock.MockTarget;
import pl.jgardo.github.repos.client.exception.HttpException;
import pl.jgardo.github.repos.client.exception.NotFoundException;
import pl.jgardo.github.repos.client.model.Repository;

public class GithubClientTest {

	private static final String EXISTING_OWNER = "velo";
	private static final String NOT_EXISTING_OWNER = "velolahsff";
	private static final String PROBLEMATIC_OWNER = "internal";
	
	private static final String EXISTING_REPO = "feign-mock";
	
	private static final String EXISTING_REPO_URL = String.format("/repos/%s/%s", EXISTING_OWNER, EXISTING_REPO);
	private static final String NOT_EXISTING_REPO_URL = String.format("/repos/%s/%s", NOT_EXISTING_OWNER, EXISTING_REPO);
	private static final String REPO_WITH_INTERNAL_PROBLEMS = String.format("/repos/%s/%s", PROBLEMATIC_OWNER, EXISTING_REPO);

	private static final String RESPONSE_DIR = "/client/response";
	
	private MockClient mockClient;
	private GithubClient githubClient;
	
	@Before
	public void setup() throws IOException {
		prepareMockClient();
		prepareGithubClient();
	}

	private void prepareMockClient() {
		mockClient = new MockClient()
				.ok(HttpMethod.GET, EXISTING_REPO_URL, "{\"id\": 55330927,\"name\": \"feign-mock\",\"full_name\": \"velo/feign-mock\",\"owner\":{\"login\": \"velo\",\"id\": 136590,\"avatar_url\": \"https://avatars0.githubusercontent.com/u/136590?v=4\",\"gravatar_id\": \"\",\"url\": \"https://api.github.com/users/velo\",\"html_url\": \"https://github.com/velo\",\"followers_url\": \"https://api.github.com/users/velo/followers\",\"following_url\": \"https://api.github.com/users/velo/following{/other_user}\",\"gists_url\": \"https://api.github.com/users/velo/gists{/gist_id}\",\"starred_url\": \"https://api.github.com/users/velo/starred{/owner}{/repo}\",\"subscriptions_url\": \"https://api.github.com/users/velo/subscriptions\",\"organizations_url\": \"https://api.github.com/users/velo/orgs\",\"repos_url\": \"https://api.github.com/users/velo/repos\",\"events_url\": \"https://api.github.com/users/velo/events{/privacy}\",\"received_events_url\": \"https://api.github.com/users/velo/received_events\",\"type\": \"User\",\"site_admin\": false},\"private\": false,\"html_url\": \"https://github.com/velo/feign-mock\",\"description\": \"An easy way to test https://github.com/Netflix/feign. Since using feign most of the logic is store into annotations this helps to check if the annotations are right.\",\"fork\": false,\"url\": \"https://api.github.com/repos/velo/feign-mock\",\"forks_url\": \"https://api.github.com/repos/velo/feign-mock/forks\",\"keys_url\": \"https://api.github.com/repos/velo/feign-mock/keys{/key_id}\",\"collaborators_url\": \"https://api.github.com/repos/velo/feign-mock/collaborators{/collaborator}\",\"teams_url\": \"https://api.github.com/repos/velo/feign-mock/teams\",\"hooks_url\": \"https://api.github.com/repos/velo/feign-mock/hooks\",\"issue_events_url\": \"https://api.github.com/repos/velo/feign-mock/issues/events{/number}\",\"events_url\": \"https://api.github.com/repos/velo/feign-mock/events\",\"assignees_url\": \"https://api.github.com/repos/velo/feign-mock/assignees{/user}\",\"branches_url\": \"https://api.github.com/repos/velo/feign-mock/branches{/branch}\",\"tags_url\": \"https://api.github.com/repos/velo/feign-mock/tags\",\"blobs_url\": \"https://api.github.com/repos/velo/feign-mock/git/blobs{/sha}\",\"git_tags_url\": \"https://api.github.com/repos/velo/feign-mock/git/tags{/sha}\",\"git_refs_url\": \"https://api.github.com/repos/velo/feign-mock/git/refs{/sha}\",\"trees_url\": \"https://api.github.com/repos/velo/feign-mock/git/trees{/sha}\",\"statuses_url\": \"https://api.github.com/repos/velo/feign-mock/statuses/{sha}\",\"languages_url\": \"https://api.github.com/repos/velo/feign-mock/languages\",\"stargazers_url\": \"https://api.github.com/repos/velo/feign-mock/stargazers\",\"contributors_url\": \"https://api.github.com/repos/velo/feign-mock/contributors\",\"subscribers_url\": \"https://api.github.com/repos/velo/feign-mock/subscribers\",\"subscription_url\": \"https://api.github.com/repos/velo/feign-mock/subscription\",\"commits_url\": \"https://api.github.com/repos/velo/feign-mock/commits{/sha}\",\"git_commits_url\": \"https://api.github.com/repos/velo/feign-mock/git/commits{/sha}\",\"comments_url\": \"https://api.github.com/repos/velo/feign-mock/comments{/number}\",\"issue_comment_url\": \"https://api.github.com/repos/velo/feign-mock/issues/comments{/number}\",\"contents_url\": \"https://api.github.com/repos/velo/feign-mock/contents/{+path}\",\"compare_url\": \"https://api.github.com/repos/velo/feign-mock/compare/{base}...{head}\",\"merges_url\": \"https://api.github.com/repos/velo/feign-mock/merges\",\"archive_url\": \"https://api.github.com/repos/velo/feign-mock/{archive_format}{/ref}\",\"downloads_url\": \"https://api.github.com/repos/velo/feign-mock/downloads\",\"issues_url\": \"https://api.github.com/repos/velo/feign-mock/issues{/number}\",\"pulls_url\": \"https://api.github.com/repos/velo/feign-mock/pulls{/number}\",\"milestones_url\": \"https://api.github.com/repos/velo/feign-mock/milestones{/number}\",\"notifications_url\": \"https://api.github.com/repos/velo/feign-mock/notifications{?since,all,participating}\",\"labels_url\": \"https://api.github.com/repos/velo/feign-mock/labels{/name}\",\"releases_url\": \"https://api.github.com/repos/velo/feign-mock/releases{/id}\",\"deployments_url\": \"https://api.github.com/repos/velo/feign-mock/deployments\",\"created_at\": \"2016-04-03T05:16:25Z\",\"updated_at\": \"2018-01-02T13:31:31Z\",\"pushed_at\": \"2017-12-20T16:49:50Z\",\"git_url\": \"git://github.com/velo/feign-mock.git\",\"ssh_url\": \"git@github.com:velo/feign-mock.git\",\"clone_url\": \"https://github.com/velo/feign-mock.git\",\"svn_url\": \"https://github.com/velo/feign-mock\",\"homepage\": \"\",\"size\": 65,\"stargazers_count\": 14,\"watchers_count\": 14,\"language\": \"Java\",\"has_issues\": true,\"has_projects\": true,\"has_downloads\": true,\"has_wiki\": true,\"has_pages\": false,\"forks_count\": 6,\"mirror_url\": null,\"archived\": false,\"open_issues_count\": 1,\"license\":{\"key\": \"other\",\"name\": \"Other\",\"spdx_id\": null,\"url\": null},\"forks\": 6,\"open_issues\": 1,\"watchers\": 14,\"default_branch\": \"master\",\"network_count\": 6,\"subscribers_count\": 3}")
				.add(HttpMethod.GET, NOT_EXISTING_REPO_URL, 
						Response.builder()
						.status(HttpStatus.SC_NOT_FOUND)
						.headers(emptyMap())
						.body("{\"message\": \"Not Found\",\"documentation_url\": \"https://developer.github.com/v3\"}", Charset.forName("UTF-8")))
				.add(HttpMethod.GET, REPO_WITH_INTERNAL_PROBLEMS, 
						Response.builder()
						.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
						.headers(emptyMap())
						.body("", Charset.forName("UTF-8")))
				;
	}

	private String getResponseFromFile(String filename) {
		try {
			return FileUtils.readFileToString(ResourceUtils.getFile(getClass().getResource(RESPONSE_DIR + "/" + filename)), Charset.forName("UTF-8"));
		} catch (IOException e) {
			throw new RuntimeException("File not found" + filename, e);
		}
	}

	private void prepareGithubClient() {
		ObjectMapper mapper = new ObjectMapper()
				   .registerModule(new ParameterNamesModule())
				   .registerModule(new Jdk8Module())
				   .registerModule(new JavaTimeModule())
				;
		
		githubClient = HystrixFeign.builder()
                .encoder(new JacksonEncoder(mapper))
                .decoder(new JacksonDecoder(mapper))
                .contract(new SpringMvcContract())
                .errorDecoder(new GithubClientErrorDecoder())
				.client(mockClient)
				.target(new MockTarget<>(GithubClient.class));
	}
	
	@Test
	public void testExistingRepo() {
		Repository repo = githubClient.retrieveRepository(EXISTING_OWNER, EXISTING_REPO);
		
		assertNotNull(repo);
		assertEquals("velo/feign-mock", repo.getFullName());
		assertEquals("An easy way to test https://github.com/Netflix/feign. Since using feign most of the logic is store into annotations this helps to check if the annotations are right.", repo.getDescription());
		assertEquals("https://github.com/velo/feign-mock.git", repo.getCloneUrl());
		assertEquals(14, repo.getStars());
		assertEquals(ZonedDateTime.parse("2016-04-03T05:16:25Z").toInstant(), repo.getCreatedAt().toInstant());
	}
	
	@Test
	public void testNotExistingRepo() {
		try {
			githubClient.retrieveRepository(NOT_EXISTING_OWNER, EXISTING_REPO);
		} catch(HystrixRuntimeException e) {
			assertTrue(e.getCause() instanceof NotFoundException);
		}
	}
	
	@Test
	public void testProblematicRepo() {
		try {
			githubClient.retrieveRepository(PROBLEMATIC_OWNER, EXISTING_REPO);
		} catch(HystrixRuntimeException e) {
			assertTrue(e.getCause() instanceof HttpException);
			HttpException httpException = (HttpException) e.getCause();
			assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, httpException.getCode());
		}
	}
}
