package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class LocationRepositoryTest {

	@Autowired
	private LocationRepository repository;

	@Test
	public void testAddLocationSuccessful() {
		Location location = new Location();
		location.setCode("MBMH_IN");
		location.setCityName("Mumbai");
		location.setRegionName("Maharashtra");
		location.setCountryCode("IN");
		location.setCountryName("India");

		Location savedLocation = repository.save(location);

		assertThat(savedLocation).isNotNull();
		assertThat(savedLocation.getCode()).isEqualTo("JP");
	}

	@Test
	public void testListSuccess() {
		List<Location> locations = repository.findUntrashed();
		assertThat(locations).isNotEmpty();
		locations.forEach(System.out::println);
	}

	@Test
	public void testGetNotFoundLocation() {
		String code = "ABCD";
		Location location = repository.findByCode(code);
		assertThat(location).isNull();
		assertThat(location.getCode()).isEqualTo(code);
	}

	@Test
	public void testTrashSuccess() {
		String code = "ABCD";
		repository.trashByCode(code);
		Location location = repository.findByCode(code);
		assertThat(location).isNull();
	}

	@Test
	public void testAddRealtimeWeatherData() {
		String code = "JP";
		Location location = repository.findByCode(code);
		RealtimeWeather realtimeWeather = location.getRealtimeWeather();

		if (realtimeWeather == null) {
			realtimeWeather = new RealtimeWeather();
			realtimeWeather.setLocation(location);
			location.setRealtimeWeather(realtimeWeather);
		}
		realtimeWeather.setTemperature(20);
		realtimeWeather.setHumidity(70);
		realtimeWeather.setPrecipipation(-70);
		realtimeWeather.setStatus("Windy");
		realtimeWeather.setWindSpeed(120);
		realtimeWeather.setLastUpdated(new Date());

		Location location2 = repository.save(location);
		assertThat(location2.getRealtimeWeather().getLocationCode()).isEqualTo(code);
	}

	@Test
	public void testAddressHourlyWeatherData() {
		Location location = repository.findById("MBMH_IN").get();
		List<HourlyWeather> hourlyWeathers = location.getListHourlyWeather();
		HourlyWeather forecast = new HourlyWeather().id(location, 10).temperature(15).precipitation(40).status("Sunny");
		HourlyWeather forecast2 = new HourlyWeather().id(location, 11).temperature(16).precipitation(50)
				.status("Cloudy");
		hourlyWeathers.add(forecast);
		hourlyWeathers.add(forecast2);

		Location updatedLocation = repository.save(location);
		assertThat(updatedLocation.getListHourlyWeather()).isNotEmpty();
	}

	@Test
	public void testFindByCountryCodeAndCityCode() {
		String countryCode = "BZ";
		String cityName = "City";
		Location location = repository.findByCountryCodeAndCityName(countryCode, cityName);
		assertThat(location).isNull();
		assertThat(location.getCountryCode()).isEqualTo(countryCode);
		assertThat(location.getCityName()).isEqualTo(cityName);
	}
}
