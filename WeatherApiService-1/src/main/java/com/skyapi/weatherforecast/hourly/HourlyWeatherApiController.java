package com.skyapi.weatherforecast.hourly;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/hourly")
@Validated
public class HourlyWeatherApiController {
	private HourlyWeatherService hourlyWeatherService;
	private GeolocationService geolocationService;
	private ModelMapper mapper;

	public HourlyWeatherApiController(HourlyWeatherService hourlyWeatherService, GeolocationService geolocationService,
			ModelMapper mapper) {
		super();
		this.hourlyWeatherService = hourlyWeatherService;
		this.geolocationService = geolocationService;
		this.mapper = mapper;
	}

	@GetMapping
	public ResponseEntity<?> listHourlyForecastByIPAddress(HttpServletRequest request) {
		String ipAddress = CommonUtility.getIPAddress(request);

		try {
			int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
			Location locationfromIp = geolocationService.getLocation(ipAddress);
			List<HourlyWeather> hourlyForecast = hourlyWeatherService.getByLocation(locationfromIp, currentHour);
			if (hourlyForecast.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(listEntity2DTO(hourlyForecast));
		} catch (NumberFormatException | GeolocationException e) {
			return ResponseEntity.badRequest().build();
		}

	}

	private List<HourlyWeather> listDTO2ListEntity(List<HourlyWeatherDTO> listDTO) {
		List<HourlyWeather> listEntity = new ArrayList<>();
		listDTO.forEach(dto -> {
			listEntity.add(mapper.map(dto, HourlyWeather.class));
		});
		return listEntity;
	}
	private HourlyWeatherListDTO listEntity2DTO(List<HourlyWeather> hourlyForecase) {
		Location location = hourlyForecase.get(0).getId().getLocation();
		HourlyWeatherListDTO listDTO = new HourlyWeatherListDTO();
		listDTO.setLocation(location.toString());
		hourlyForecase.forEach(hourlyWeather -> {
			HourlyWeatherDTO dto = mapper.map(hourlyWeather, HourlyWeatherDTO.class);
			listDTO.addHourlyWeatherDTO(dto);
		});
		return listDTO;
	}

	@GetMapping("/{locationCode}")
	public ResponseEntity<?> listHourlyForecastByLocationCode(@PathVariable("locationCode") String locationCode,
			HttpServletRequest request) {
		try {
			int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
			List<HourlyWeather> hourlyForecast = hourlyWeatherService.getByLocationCode(locationCode, currentHour);
			if (hourlyForecast.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(listEntity2DTO(hourlyForecast));
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping("/{locationCode}")
	public ResponseEntity<?> updateHourlyForecast(@PathVariable("locationCode") String locationCode,
			@RequestBody @Valid List<HourlyWeatherDTO> listDTO) throws BadRequestException {
		if (listDTO.isEmpty()) {
			throw new BadRequestException("Hourly forecast data cannot be empty");
		}
		listDTO.forEach(System.out::println);
		List<HourlyWeather> listHourlyWeather =  listDTO2ListEntity(listDTO);
		listHourlyWeather.forEach(System.out::println);
			List<HourlyWeather> updateHourlyWeather = hourlyWeatherService.updateByLocationCode(locationCode, listHourlyWeather);
			return ResponseEntity.ok(listEntity2DTO(updateHourlyWeather));
		
	}

}
