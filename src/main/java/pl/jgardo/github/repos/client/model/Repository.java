package pl.jgardo.github.repos.client.model;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Repository {
	private static final String FULL_NAME = "full_name";
	private static final String DESCRIPTION = "description";
	private static final String CLONE_URL = "clone_url";
	private static final String STARS = "stargazers_count";
	private static final String CREATED_AT = "created_at";

	private final String fullName;
	private final String description;
	private final String cloneUrl;
	private final int stars;
	private final ZonedDateTime createdAt;

	@JsonCreator
	public Repository(
			@JsonProperty(FULL_NAME) String fullName, 
			@JsonProperty(DESCRIPTION) String description,
			@JsonProperty(CLONE_URL) String cloneUrl, 
			@JsonProperty(STARS) int stars, 
			@JsonProperty(CREATED_AT) ZonedDateTime createdAt) {
		this.fullName = fullName;
		this.description = description;
		this.cloneUrl = cloneUrl;
		this.stars = stars;
		this.createdAt = createdAt;
	}
	public String getFullName() {
		return fullName;
	}
	public String getDescription() {
		return description;
	}
	public String getCloneUrl() {
		return cloneUrl;
	}
	public int getStars() {
		return stars;
	}
	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}
}
