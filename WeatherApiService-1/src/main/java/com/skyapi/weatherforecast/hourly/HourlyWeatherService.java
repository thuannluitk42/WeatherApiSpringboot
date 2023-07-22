package com.skyapi.weatherforecast.hourly;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;

@Service
public class HourlyWeatherService {
	private HourlyWeatherRepository repo;
	private LocationRepository locationRepository;

	public HourlyWeatherService(HourlyWeatherRepository repo, LocationRepository locationRepository) {
		super();
		this.repo = repo;
		this.locationRepository = locationRepository;
	}

	public List<HourlyWeather> getByLocation(Location location, int currentHour) throws LocationNotFoundException {
		String countryCode = location.getCountryCode();
		String cityName = location.getCityName();
		Location locationInDB = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);
		if (locationInDB == null) {
			throw new LocationNotFoundException(
					"No location found with the given country code: " + countryCode + " and city name: " + cityName);
		}
		return repo.findByLocationCode(locationInDB.getCode(), currentHour);

	}

	public List<HourlyWeather> getByLocationCode(String locationCode, int currentHour)
			throws LocationNotFoundException {
		Location locationInDB = locationRepository.findByCode(locationCode);
		if (locationInDB == null) {
			throw new LocationNotFoundException("No location found with the given code: " + locationCode);
		}
		return repo.findByLocationCode(locationCode, currentHour);

	}

	public List<HourlyWeather> updateByLocationCode(String locationCode, List<HourlyWeather> hourlyForecastInRequest)
			throws LocationNotFoundException {
		Location location = locationRepository.findByCode(locationCode);
		if (location == null) {
			throw new LocationNotFoundException("No location found with the given code: " + locationCode);
		}
		for (HourlyWeather item : hourlyForecastInRequest) {
			item.getId().setLocation(location);
		}
		List<HourlyWeather> hourlyWeatherInDB = location.getListHourlyWeather();
		List<HourlyWeather> hourlyWeatherToBeRemoved = new ArrayList<>();

		for (HourlyWeather item : hourlyWeatherInDB) {
			if (!hourlyForecastInRequest.contains(item)) {
				hourlyWeatherToBeRemoved.add(item.getShallowCopy());
			}
		}
		for (HourlyWeather item : hourlyWeatherToBeRemoved) {
			hourlyWeatherInDB.remove(item);
		}
		return (List<HourlyWeather>) repo.saveAll(hourlyForecastInRequest);
	}
}
