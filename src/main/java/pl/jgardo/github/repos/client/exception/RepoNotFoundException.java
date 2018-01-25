package pl.jgardo.github.repos.client.exception;

public class RepoNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 2472563516875986387L;
	private final String owner;
	private final String repoName;

	public RepoNotFoundException(String owner, String repoName) {
		this.owner = owner;
		this.repoName = repoName;
	}

	public String getRepoName() {
		return repoName;
	}

	public String getOwner() {
		return owner;
	}
}
