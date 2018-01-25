package pl.jgardo.github.repos.controller.dto;

public class RepositoryDTO {
	private final String fullName;
	private final String description;
	private final String cloneUrl;
	private final int stars;
	private final String createdAt;

	public RepositoryDTO(String fullName, String description, String cloneUrl, int stars, String createdAt) {
		super();
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
	public String getCreatedAt() {
		return createdAt;
	}
}
