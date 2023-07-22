package com.skyapi.weatherforecast;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.skyapi.weatherforecast.common.Location;

@Service
public class GeolocationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationService.class);
	private String DBPath = "/ip2locationdb/IP2LOCATION-LITE-DB3.BIN";
	private IP2Location ip2Location = new IP2Location();

	public GeolocationService() {
		try {
			InputStream inputStream = getClass().getResourceAsStream(DBPath);
			byte[] data = inputStream.readAllBytes();
			ip2Location.Open(DBPath);
			inputStream.close();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(),e);
		}
	}
	
	public Location getLocation(String ipAddress) {
		try {
			IPResult ipResult = ip2Location.IPQuery(ipAddress);
			if (!"OK".equals(ipResult.getStatus())) {
				throw new GeolocationException("Geolocation failed with status: "+ipResult.getStatus());
			}
			return new Location(ipResult.getCity(), ipResult.getRegion(), ipResult.getCountryLong(), ipResult.getCountryShort());
		} catch (Exception e) {
			throw new GeolocationException("Error querying IP Database: "+ e);
		}
	}

}
