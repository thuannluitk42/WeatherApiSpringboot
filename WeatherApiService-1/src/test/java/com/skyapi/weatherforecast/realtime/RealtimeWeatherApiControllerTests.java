package com.skyapi.weatherforecast.realtime;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

@WebMvcTest(RealtimeWeatherApiController.class)
public class RealtimeWeatherApiControllerTests {
	private static final String END_POINT_PATH = "/v1/realtime";

	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper mapper;
	@MockBean
	RealtimeWeatherService realtimeWeatherService;
	@MockBean
	GeolocationService geolocationService;

	@Test
	public void testGetShouldReturnStatus400BadRequest() throws Exception {
		Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenThrow(GeolocationException.class);
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	public void testGetShouldReturnStatus400NotFound() throws Exception {
		Location location = new Location();
		location.setCountryCode("US");
		location.setCityName("Pushup");
		LocationNotFoundException exception = new LocationNotFoundException(location.getCountryCode(),
				location.getCityName());
		Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
		Mockito.when(realtimeWeatherService.getByLocation(location)).thenThrow(exception);
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errors[0]", is(exception.getMessage()))).andDo(print());
	}

	@Test
	public void testGetShouldReturnStatus200isOK() throws Exception {
		Location location = new Location();
		location.setCode("JP");
		location.setCityName("Tokyo");
		location.setRegionName("Tokyo");
		location.setCountryName("Japan");
		location.setCountryCode("JP");

		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(120);
		realtimeWeather.setPrecipipation(-70);
		realtimeWeather.setStatus("Storm");
		realtimeWeather.setWindSpeed(541);
		realtimeWeather.setLastUpdated(new Date());

		realtimeWeather.setLocation(location);
		location.setRealtimeWeather(realtimeWeather);

		Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
		Mockito.when(realtimeWeatherService.getByLocation(location)).thenReturn(realtimeWeather);

		String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", "
				+ location.getCountryName();
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.location", is(expectedLocation))).andDo(print());
	}

	@Test
	public void testGetByLocationCodeShouldReturnStatus404NotFound() throws Exception {
		String locationCode = "JP";
		Mockito.when(realtimeWeatherService.getByLocationCode(locationCode)).thenThrow(LocationNotFoundException.class);
		mockMvc.perform(get(END_POINT_PATH + "/JP")).andExpect(status().isNotFound()).andDo(print());
	}

	@Test
	public void testGetByLocationCodeShouldReturnStatus200OK() throws Exception {
		String locationCode = "SFCA_USA";
		Location location = new Location();
		location.setCode(locationCode);
		location.setCityName("San Franciso");
		location.setRegionName("California");
		location.setCountryName("United States of America");
		location.setCountryCode("US");

		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(120);
		realtimeWeather.setPrecipipation(-70);
		realtimeWeather.setStatus("Storm");
		realtimeWeather.setWindSpeed(541);
		realtimeWeather.setLastUpdated(new Date());

		realtimeWeather.setLocation(location);
		location.setRealtimeWeather(realtimeWeather);

		Mockito.when(realtimeWeatherService.getByLocationCode(locationCode)).thenReturn(realtimeWeather);

		String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", "
				+ location.getCountryName();
		String requestAPI = END_POINT_PATH + "/" + locationCode;
		mockMvc.perform(get(requestAPI)).andExpect(status().isOk()).andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.location", is(expectedLocation))).andDo(print());
	}

	@Test
	public void testUpdateReturn400BadRequest() throws Exception {
		String locationCode = "ABC_USA";
		String requestAPI = END_POINT_PATH + "/" + locationCode;

		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(120);
		realtimeWeather.setPrecipipation(-70);
		realtimeWeather.setStatus("Storm");
		realtimeWeather.setWindSpeed(541);
		realtimeWeather.setLastUpdated(new Date());

		String bodyContent = mapper.writeValueAsString(realtimeWeather);

		mockMvc.perform(put(requestAPI).contentType("application/json").content(bodyContent))
				.andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	public void testUpdateReturn404NotFound() throws Exception {
		String locationCode = "ABC_USA";
		String requestAPI = END_POINT_PATH + "/" + locationCode;

		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(32);
		realtimeWeather.setPrecipipation(88);
		realtimeWeather.setStatus("Storm");
		realtimeWeather.setWindSpeed(10);
		realtimeWeather.setLocationCode(locationCode);

		LocationNotFoundException exception = new LocationNotFoundException(locationCode);
		Mockito.when(realtimeWeatherService.update(locationCode, realtimeWeather)).thenThrow(exception);

		String bodyContent = mapper.writeValueAsString(realtimeWeather);

		mockMvc.perform(put(requestAPI).contentType("application/json").content(bodyContent))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.errors[0]", is(exception.getMessage())))
				.andDo(print());
	}

	@Test
	public void testUpdateShouldReturn200OK() throws Exception {
		String locationCode = "JP";
		String requestAPI = END_POINT_PATH + "/" + locationCode;

		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(71);
		realtimeWeather.setPrecipipation(88);
		realtimeWeather.setStatus("Storm");
		realtimeWeather.setWindSpeed(10);
		realtimeWeather.setLocationCode(locationCode);

		Mockito.when(realtimeWeatherService.update(locationCode, realtimeWeather)).thenReturn(realtimeWeather);

		String bodyContent = mapper.writeValueAsString(realtimeWeather);

		mockMvc.perform(put(requestAPI).contentType("application/json").content(bodyContent)).andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.temperature", is(12)))
				.andExpect(jsonPath("$.wind_speed", is(10)))
				.andExpect(jsonPath("$.precipipation", is(88)))
				.andDo(print());
	}
}
