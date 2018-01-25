package pl.jgardo.github.repos.timezone;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

import pl.jgardo.github.repos.timezone.exception.InvalidTimeZoneIdException;

@Service
public class TimeZoneService {

	private static final Set<String> AVAILABLE_TIME_ZONE_IDS = new HashSet<>(Arrays.asList(TimeZone.getAvailableIDs()));
	
	public ZoneId getZoneIdByTimeZoneId(String timeZoneId) {
		assertTimeZoneIdIsValid(timeZoneId); 
		
		TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
		return timeZone.toZoneId();
	}

	private void assertTimeZoneIdIsValid(String timeZoneId) {
		if (!AVAILABLE_TIME_ZONE_IDS.contains(timeZoneId)) {
			throw new InvalidTimeZoneIdException(timeZoneId);
		}
	}
	
}
