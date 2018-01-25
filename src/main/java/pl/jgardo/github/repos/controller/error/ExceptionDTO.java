package pl.jgardo.github.repos.controller.error;


public class ExceptionDTO {
	private final ExceptionCode exceptionCode;
	private final String description;
	
	ExceptionDTO(ExceptionCode exceptionCode, String description) {
		this.exceptionCode = exceptionCode;
		this.description = description;
	}

	public ExceptionCode getExceptionCode() {
		return exceptionCode;
	}

	public String getDescription() {
		return description;
	}
}