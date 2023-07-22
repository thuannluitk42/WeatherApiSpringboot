package com.skyapi.weatherforecast.realtime;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;

@Service
public class RealtimeWeatherService {
	private RealtimeWeatherRepository realtimeWeatherRepository;
	private LocationRepository locationRepository;

	public RealtimeWeatherService(RealtimeWeatherRepository repository) {
		this.realtimeWeatherRepository = repository;
	}

	public RealtimeWeather getByLocation(Location location) {
		String countryCode = location.getCountryCode();
		String cityName = location.getCityName();
		RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByCountryCodeAndCity(countryCode, cityName);
		if (realtimeWeather == null) {
			throw new LocationNotFoundException(countryCode, cityName);
		}
		return realtimeWeather;
	}

	public RealtimeWeather getByLocationCode(String locationCode) {
		RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByLocationCode(locationCode);
		if (realtimeWeather == null) {
			throw new LocationNotFoundException(locationCode);
		}
		return realtimeWeather;
	}

	public RealtimeWeather update(String locationCode, RealtimeWeather realtimeWeather){
		Location location = locationRepository.findByCode(locationCode);
		if (location == null) {
			throw new LocationNotFoundException(locationCode);
		}
		realtimeWeather.setLocation(location);
		realtimeWeather.setLastUpdated(new Date());
		if (location.getRealtimeWeather() == null) {
			location.setRealtimeWeather(realtimeWeather);
			Location updateLocation = locationRepository.save(location);
			return updateLocation.getRealtimeWeather();
		}
		return realtimeWeatherRepository.save(realtimeWeather);
	}
}
