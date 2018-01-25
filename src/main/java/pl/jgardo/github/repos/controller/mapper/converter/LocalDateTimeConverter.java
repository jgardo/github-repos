package pl.jgardo.github.repos.controller.mapper.converter;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

@Component
public class LocalDateTimeConverter implements Converter<LocalDateTime, String>{

	@Override
	public String toDtoFormat(LocalDateTime value) {
		return value != null
				? value.toString()
				: null;
	}	
}
