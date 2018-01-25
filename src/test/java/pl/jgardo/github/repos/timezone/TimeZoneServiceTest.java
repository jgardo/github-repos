package pl.jgardo.github.repos.timezone;

import static org.junit.Assert.assertEquals;

import java.time.ZoneId;
import java.util.TimeZone;

import org.junit.Test;

import pl.jgardo.github.repos.timezone.exception.InvalidTimeZoneIdException;

public class TimeZoneServiceTest {

	public static final String NON_EXISTING_TIME_ZONE = "nonExisting/TimeZone";
	public static final String EUROPE_WARSAW = "Europe/Warsaw";
	
	private TimeZoneService timeZoneService = new TimeZoneService();
	
	@Test
	public void testExistingTimeZone() {
		// given
		
		// when
		ZoneId zoneId = timeZoneService.getZoneIdByTimeZoneId(EUROPE_WARSAW);
		
		// then
		assertEquals(TimeZone.getTimeZone(EUROPE_WARSAW).toZoneId(), zoneId);
	}

	@Test(expected=InvalidTimeZoneIdException.class)
	public void testNotExistingTimeZone() {
		// given
		
		// when
		timeZoneService.getZoneIdByTimeZoneId(NON_EXISTING_TIME_ZONE);
		
		// then
		// expected exception
	}
}
