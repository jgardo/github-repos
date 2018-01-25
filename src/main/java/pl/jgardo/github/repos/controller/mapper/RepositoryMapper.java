package pl.jgardo.github.repos.controller.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import pl.jgardo.github.repos.client.model.Repository;
import pl.jgardo.github.repos.controller.dto.RepositoryDTO;
import pl.jgardo.github.repos.controller.mapper.converter.LocalDateTimeConverter;

@Component
public class RepositoryMapper {

	private final LocalDateTimeConverter localDateTimeConverter;
	
	@Autowired
	public RepositoryMapper(LocalDateTimeConverter localDateTimeConverter) {
		this.localDateTimeConverter = localDateTimeConverter;
	}

	public RepositoryDTO convertToDtoUsingZoneId(Repository repository, ZoneId zoneId) {
		Assert.notNull(zoneId, "timeZone is required");
		Assert.notNull(repository, "repository is required");
		
		LocalDateTime createdAt = getCreatedAtInZoneId(repository, zoneId);

		return createDto(repository, createdAt);
	}

	private LocalDateTime getCreatedAtInZoneId(Repository repository, ZoneId zoneId) {
		ZonedDateTime zonedCreatedAt = repository.getCreatedAt();
		return zonedCreatedAt != null
				? zonedCreatedAt.withZoneSameInstant(zoneId).toLocalDateTime()
				: null;
	}

	private RepositoryDTO createDto(Repository repository, LocalDateTime createdAt) {

		return new RepositoryDTO(
				repository.getFullName(),
				repository.getDescription(),
				repository.getCloneUrl(),
				repository.getStars(),
				localDateTimeConverter.toDtoFormat(createdAt));
	}
	
}
