package pl.jgardo.github.repos.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockserver.integration.ClientAndProxy.startClientAndProxy;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndProxy;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import pl.jgardo.github.repos.GithubReposApplication;
import pl.jgardo.github.repos.controller.error.ExceptionCode;
import pl.jgardo.github.repos.timezone.TimeZoneServiceTest;

@RunWith(SpringRunner.class)
@SpringBootTest(
  webEnvironment = WebEnvironment.RANDOM_PORT,
  classes = GithubReposApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {"github.rest.url=localhost:1080",
		"feign.hystrix.enable=true","feign.httpclient.enabled=true",
"hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=4000"})
public class OwnerRepoControllerIntegrationTest {

	private static final String VALID_REPOSITORY_MOJOMBO = "/repositories/mojombo/asteroids";

	private static final String NOT_EXISTING_OWNER_NAME = "/repositories/nal-ghnvknf-yr881-yr8j-FASD/asteroids";
	private static final String NOT_EXISTING_REPOSITORY_NAME = "/repositories/mojombo/._n-al-ghnvknf-yr881-yr8j-FA.S_D-";

	@Autowired
	private MockMvc mvc;

	private static ClientAndProxy proxy;
	private static ClientAndServer mockServer;
	
	@BeforeClass
	public static void startProxy() {
	    mockServer = startClientAndServer(1080);
	    proxy = startClientAndProxy(1090);
	    
	    mockServer.when(
	            request()
	                .withMethod("GET")
	                .withPath("/repos/mojombo/asteroids")
		        ).respond(
		            response()
		                .withStatusCode(200)
		                .withBody("{\"full_name\": \"mojombo/asteroids\",\"description\": \"Destroy your Atom editor, Asteroids style!\",\"clone_url\": \"https://github.com/mojombo/asteroids.git\",\"stargazers_count\": 96,\"created_at\": \"2014-03-03T07:40:00Z\"}")
		                .withHeaders(
			                    new Header("content-type", "application/json")
			                )
		        );
	    
	    mockServer.when(
	            request()
	                .withMethod("GET")
	                .withPath("/repos/mojombo/asteroids")
	                .withHeaders(
	                    new Header("Time-Zone", TimeZoneServiceTest.NON_EXISTING_TIME_ZONE)
	                )
		        ).respond(
		            response()
		                .withStatusCode(200)
		                .withBody("{\"full_name\": \"mojombo/asteroids\",\"description\": \"Destroy your Atom editor, Asteroids style!\",\"clone_url\": \"https://github.com/mojombo/asteroids.git\",\"stargazers_count\": 96,\"created_at\": \"2014-03-03T07:40:00Z\"}")
		                .withHeaders(
			                    new Header("content-type", "application/json")
			                )
		        );
	    mockServer.when(
	            request()
	                .withMethod("GET")
	                .withPath("/repos/nal-ghnvknf-yr881-yr8j-FASD/asteroids")
	        ).respond(
	            response()
	                .withStatusCode(404)
	                .withBody("{\"message\": \"Not Found\",\"documentation_url\": \"https://developer.github.com/v3\"}")
	                .withHeaders(
		                    new Header("content-type", "application/json")
		                )

	        );
	
	    mockServer.when(
	            request()
	                .withMethod("GET")
	                .withPath("/repos/mojombo/._n-al-ghnvknf-yr881-yr8j-FA.S_D-")
	        ).respond(
	            response()
	                .withStatusCode(404)
	                .withBody("{\"message\": \"Not Found\",\"documentation_url\": \"https://developer.github.com/v3\"}")
	                .withHeaders(
		                    new Header("content-type", "application/json")
		                )
	        );
	
	    mockServer.when(
	            request()
	                .withMethod("GET")
	                .withPath("/users/mojombo")
	        ).respond(
	            response()
	                .withStatusCode(200)
	                .withHeaders(
		                    new Header("content-type", "application/json")
		                )
	        );
	
	}
	
	@AfterClass
	public static void stopProxy() {
	    proxy.stop();
	    mockServer.stop();
	}

	@Ignore("Doesn't work after migration to RxJava")
	@Test
	public void testValidRequest() throws Throwable {
		mvc.perform(get(VALID_REPOSITORY_MOJOMBO)
						.header(OwnerRepoController.TIME_ZONE_HEADER_NAME, TimeZoneServiceTest.EUROPE_WARSAW)
				  .contentType(MediaType.APPLICATION_JSON))
		
				  .andExpect(status().isOk())
				  .andExpect(content()
				  .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				  .andExpect(jsonPath("$.fullName", is("mojombo/asteroids")))
				  .andExpect(jsonPath("$.description", is("Destroy your Atom editor, Asteroids style!")))
				  .andExpect(jsonPath("$.cloneUrl", is("https://github.com/mojombo/asteroids.git")))
				  .andExpect(jsonPath("$.stars", is(96)))
				  .andExpect(jsonPath("$.createdAt", is("2014-03-03T08:40")))
				  ;
	}

	@Test
	public void testTimeZoneHeaderMissing() throws Throwable {
		mvc.perform(get(VALID_REPOSITORY_MOJOMBO)
				  .contentType(MediaType.APPLICATION_JSON))
		
				  .andExpect(status().isBadRequest())
				  .andExpect(content()
				  .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				  .andExpect(jsonPath("$.description", notNullValue()))
				  .andExpect(jsonPath("$.exceptionCode", is(ExceptionCode.INVALID_TIME_ZONE_HEADER.name())))
				  ;
	}

	@Test
	public void testTimeZoneHeaderInvalid() throws Throwable {
		mvc.perform(get(VALID_REPOSITORY_MOJOMBO)
				  .contentType(MediaType.APPLICATION_JSON)
				  		.header(OwnerRepoController.TIME_ZONE_HEADER_NAME, TimeZoneServiceTest.NON_EXISTING_TIME_ZONE))
		
				  .andExpect(status().isBadRequest())
				  .andExpect(content()
				  .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				  .andExpect(jsonPath("$.description", notNullValue()))
				  .andExpect(jsonPath("$.exceptionCode", is(ExceptionCode.INVALID_TIME_ZONE_HEADER.name())))
				  ;
	}

	@Ignore("Doesn't work after migration to RxJava")
	@Test
	public void testNotExistingOwner() throws Throwable {
		mvc.perform(get(NOT_EXISTING_OWNER_NAME)
				  .contentType(MediaType.APPLICATION_JSON)
				  		.header(OwnerRepoController.TIME_ZONE_HEADER_NAME, TimeZoneServiceTest.EUROPE_WARSAW))
		
				  .andExpect(status().isNotFound())
				  .andExpect(content()
				  .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				  .andExpect(jsonPath("$.description", notNullValue()))
				  .andExpect(jsonPath("$.exceptionCode", is(ExceptionCode.REPOSITORY_NOT_FOUND.name())))
				  ;
	}

	@Ignore("Doesn't work after migration to RxJava")
	@Test
	public void testNotExistingRepo() throws Throwable {
		mvc.perform(get(NOT_EXISTING_REPOSITORY_NAME)
				  .contentType(MediaType.APPLICATION_JSON)
				  		.header(OwnerRepoController.TIME_ZONE_HEADER_NAME, TimeZoneServiceTest.EUROPE_WARSAW))
		
				  .andExpect(status().isNotFound())
				  .andExpect(content()
				  .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				  .andExpect(jsonPath("$.description", notNullValue()))
				  .andExpect(jsonPath("$.exceptionCode", is(ExceptionCode.REPOSITORY_NOT_FOUND.name())))
				  ;
	}
}
