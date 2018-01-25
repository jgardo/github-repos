package pl.jgardo.github.repos.controller.error;

import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import pl.jgardo.github.repos.client.exception.RepoNotFoundException;
import pl.jgardo.github.repos.controller.OwnerRepoController;
import pl.jgardo.github.repos.timezone.exception.InvalidTimeZoneIdException;

@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {
	
	private static final String LIST_OF_TZ_DATABASE_TIME_ZONES_URL = "https://en.wikipedia.org/wiki/List_of_tz_database_time_zones";

	private static final Log LOG = LogFactory.getLog(GlobalControllerExceptionHandler.class);

	@ExceptionHandler(InvalidTimeZoneIdException.class)
	public ResponseEntity<Object> handleInvalidZoneId(InvalidTimeZoneIdException ex, WebRequest request) {
		String description = prepareDescription(ex);
	
		ExceptionDTO exceptionDto = new ExceptionDTO(ExceptionCode.INVALID_TIME_ZONE_HEADER, description);
		return handleInternal(ex, request, exceptionDto, HttpStatus.BAD_REQUEST);
	}

	private String prepareDescription(InvalidTimeZoneIdException ex) {
		Optional<String> headerValue = ex.getHeaderValue();
		
		String availableTimeZonesInfo = String.format("Valid timezones are listed in '%s'.", LIST_OF_TZ_DATABASE_TIME_ZONES_URL);
		if (!headerValue.isPresent()) {
			return String.format("Header '%s' is missing. %s", 
					OwnerRepoController.TIME_ZONE_HEADER_NAME,
					availableTimeZonesInfo);
		} else {
			return String.format("Header '%s' is invalid. Given header's value is %s. %s", 
					OwnerRepoController.TIME_ZONE_HEADER_NAME, 
					headerValue.get(),
					availableTimeZonesInfo);
		}
	}

	@ExceptionHandler(RepoNotFoundException.class)
	public ResponseEntity<Object> handleRepoNotFound (RepoNotFoundException ex, WebRequest request) {
		String description = String.format("Repo '%s' for owner '%s' does not exist.", ex.getRepoName(), ex.getOwner());
	
		ExceptionDTO exceptionDto = new ExceptionDTO(ExceptionCode.REPOSITORY_NOT_FOUND, description);
		return handleInternal(ex, request, exceptionDto, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
		String description = ex.getMessage();
	
		ExceptionDTO exceptionDto = new ExceptionDTO(ExceptionCode.ILLEGAL_ARGUMENT, description);
		return handleInternal(ex, request, exceptionDto, HttpStatus.BAD_REQUEST);
	}
		
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
		LOG.error("Internal error.", ex);
		
		String description = "Internal error";
	
		ExceptionDTO exceptionDto = new ExceptionDTO(ExceptionCode.INTERNAL_ERROR, description);
		return handleInternal(ex, request, exceptionDto, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	private ResponseEntity<Object> handleInternal(Exception ex, WebRequest request,
			ExceptionDTO exceptionDto, HttpStatus httpStatus) {
		return handleExceptionInternal(ex, exceptionDto, new HttpHeaders(), httpStatus, request);
	}

}