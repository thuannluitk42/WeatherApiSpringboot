package com.skyapi.weatherforecast.hourly;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.HourlyWeatherId;
import com.skyapi.weatherforecast.common.Location;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class HourlyWeatherRepositoryTests {
	@Autowired
	private HourlyWeatherRepository hourlyWeatherRepository;

	@Test
	public void testAdd() {
		String locationCode = "DELHI_IN";
		int hourOfDay = 12;
		Location location = new Location().code(locationCode);
		HourlyWeather hourlyWeather = new HourlyWeather().location(location).hourOfDay(hourOfDay).temperature(13)
				.precipitation(70).status("Cloudy");
		HourlyWeather updateForecast = hourlyWeatherRepository.save(hourlyWeather);
		assertThat(updateForecast.getId().getLocation().getCode()).isEqualTo(locationCode);
		assertThat(updateForecast.getId().getHourOfDay()).isEqualTo(hourOfDay);
	}

	@Test
	public void testDelete() {
		Location location = new Location().code("DELHI_IN");
		HourlyWeatherId id = new HourlyWeatherId(0, location);
		hourlyWeatherRepository.deleteById(id);
		Optional<HourlyWeather> result = hourlyWeatherRepository.findById(id);
		assertThat(result).isNotPresent();
	}

	@Test
	public void testFindByLocationCode() {
		String locationCode = "DELHI_IN";
		int currentHour = 10;
		List<HourlyWeather> hourlyForecast = hourlyWeatherRepository.findByLocationCode(locationCode, currentHour);
		assertThat(hourlyForecast).isNotEmpty();
	}
}
