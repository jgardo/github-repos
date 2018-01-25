package pl.jgardo.github.repos.client.exception;

public class NotFoundException extends HttpException {
	private static final long serialVersionUID = -814386194435510871L;

	public NotFoundException(String url) {
		super(url, 404);
	}
	public String getUrl() {
		return url;
	}
}
