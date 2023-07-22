package com.skyapi.weatherforecast.location;

public class LocationNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LocationNotFoundException(String locationCode) {
		super("No location found with the given code: " + locationCode);
	}

	public LocationNotFoundException(String locationCode, String cityName) {
		super("No location found with the given country code: " + locationCode + " and city name: " + cityName);
	}

}
