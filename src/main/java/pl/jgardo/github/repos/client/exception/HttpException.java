package pl.jgardo.github.repos.client.exception;

public class HttpException extends RuntimeException {
	private static final long serialVersionUID = -6523652477526613127L;
	final String url;
	private final int code;

	public HttpException(String url, int code) {
		this.url = url;
		this.code = code;
	}
	public String getUrl() {
		return url;
	}
	public int getCode() {
		return code;
	}
}
