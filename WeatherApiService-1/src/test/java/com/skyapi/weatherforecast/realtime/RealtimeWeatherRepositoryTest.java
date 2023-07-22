package com.skyapi.weatherforecast.realtime;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.skyapi.weatherforecast.common.RealtimeWeather;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RealtimeWeatherRepositoryTest {

	@Autowired
	private RealtimeWeatherRepository repo;

	@Test
	public void testUpdate() {
		String code = "JP";
		RealtimeWeather realtimeWeather = repo.findById(code).get();
		realtimeWeather.setTemperature(20);
		realtimeWeather.setHumidity(70);
		realtimeWeather.setPrecipipation(-70);
		realtimeWeather.setStatus("Windy");
		realtimeWeather.setWindSpeed(120);
		realtimeWeather.setLastUpdated(new Date());
		RealtimeWeather updateRealtimeWeather = repo.save(realtimeWeather);

		assertThat(updateRealtimeWeather.getHumidity()).isEqualTo(70);
	}

	@Test
	public void testFindByCountryCodeAndCityNotFound() {
		String countryCode = "JP";
		String cityName = "Tokyo";

		RealtimeWeather realtimeWeather = repo.findByCountryCodeAndCity(countryCode, cityName);
		assertThat(realtimeWeather).isNull();
		assertThat(realtimeWeather.getLocation().getCityName()).isEqualTo(cityName);
	}

	@Test
	public void testFindByLocationNotFound() {
		String locationCode = "ABCXYZ";
		RealtimeWeather realtimeWeather = repo.findByLocationCode(locationCode);
		assertThat(realtimeWeather).isNull();
	}

	@Test
	public void testFindByTrashedLocationNotFound() {
		String locationCode = "JP_1111";
		RealtimeWeather realtimeWeather = repo.findByLocationCode(locationCode);
		assertThat(realtimeWeather).isNull();
	}

	@Test
	public void testFindByLocationFound() {
		String locationCode = "JP";
		RealtimeWeather realtimeWeather = repo.findByLocationCode(locationCode);
		assertThat(realtimeWeather).isNotNull();
		assertThat(realtimeWeather.getLocationCode()).isEqualTo(locationCode);
	}
}
