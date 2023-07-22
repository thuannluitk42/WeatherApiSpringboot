package com.skyapi.weatherforecast.realtime;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/realtime")
public class RealtimeWeatherApiController {
	private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeWeatherApiController.class);
	private GeolocationService geolocationService;
	private RealtimeWeatherService realtimeWeatherService;
	private ModelMapper mapper;

	public RealtimeWeatherApiController(GeolocationService geolocationService,
			RealtimeWeatherService realtimeWeatherService, ModelMapper modelMapper) {
		this.geolocationService = geolocationService;
		this.realtimeWeatherService = realtimeWeatherService;
		this.mapper = modelMapper;
	}

	@GetMapping
	public ResponseEntity<?> getRealtimeWeatherByIPAddress(HttpServletRequest request) {
		String ipAddress = CommonUtility.getIPAddress(request);
		try {
			Location locationFromIP = geolocationService.getLocation(ipAddress);
			RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocation(locationFromIP);
			return ResponseEntity.ok(convert2DTO(realtimeWeather));
		} catch (GeolocationException e) {
			LOGGER.error(e.getMessage(), e);
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/{locationCode}")
	public ResponseEntity<?> getRealtimeByLocationCode(@PathVariable("locationCode") String locationCode) {
		RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocationCode(locationCode);
		return ResponseEntity.ok(convert2DTO(realtimeWeather));
	}

	@PutMapping("/{locationCode}")
	public ResponseEntity<?> updateRealtimeWeather(@PathVariable("locationCode") String locationCode,
			@RequestBody @Valid RealtimeWeather realtimeWeather) {
		realtimeWeather.setLocationCode(locationCode);
		RealtimeWeather updateRealtimeWeather = realtimeWeatherService.update(locationCode, realtimeWeather);
		return ResponseEntity.ok(convert2DTO(updateRealtimeWeather));
	}

	private RealtimeWeatherDTO convert2DTO(RealtimeWeather realtimeWeather) {
		return mapper.map(realtimeWeather, RealtimeWeatherDTO.class);
	}
}
