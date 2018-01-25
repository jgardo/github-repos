package pl.jgardo.github.repos.timezone.exception;

import java.util.Optional;

public class InvalidTimeZoneIdException extends RuntimeException {

	private static final long serialVersionUID = 2659736840829921477L;

	private String headerValue;
	
	public InvalidTimeZoneIdException(Throwable cause, String headerValue) {
		super(cause);
		this.headerValue = headerValue;
	}

	public InvalidTimeZoneIdException(String headerValue) {
		this.headerValue = headerValue;
	}

	public Optional<String> getHeaderValue() {
		return Optional.ofNullable(headerValue);
	}
}
