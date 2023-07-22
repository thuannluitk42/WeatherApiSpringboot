package com.skyapi.weatherforecast.hourly;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

@WebMvcTest(HourlyWeatherApiController.class)
public class HourlyWeatherApiControllerTests {
	private static final String END_POINT_PATH = "/v1/hourly";
	private static final String X_CURRENT_HOUR = "X-Current-Hour";

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private HourlyWeatherService hourlyWeatherService;
	@MockBean
	private GeolocationService geolocationService;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void testGetByIPShouldReturn400BadRequestBecauseNoHeaderXCurrentHour() throws Exception {
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	public void testGetByIPShouldReturn400BadRequestBecauseGeolocationException() throws Exception {
		GeolocationException ex = new GeolocationException("Geolocation error");
		Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenThrow(ex);
		mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, "9"))
				.andExpect(status().isBadRequest())
				.andDo(print());
	}

	@Test
	public void testGetByIPShouldReturn404NotFound() throws Exception {
		Location location = new Location().code("DELHIABC");
		int currentHour = 9;
		when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
		LocationNotFoundException ex = new LocationNotFoundException(location.getCode());
		when(hourlyWeatherService.getByLocation(location, currentHour)).thenThrow(ex);
		mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errors[0]",is(ex.getMessage())))
				.andDo(print());
	}

	@Test
	public void testGetByIPShouldReturn204NoContent() throws Exception {
		int currentHour = 9;
		Location location = new Location().code("DELHI_IN");
		Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
		when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(new ArrayList<>());
		mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
				.andExpect(status().isNoContent()).andDo(print());
	}

	@Test
	public void testGetByIPShouldReturn200OK() throws Exception {
		int currentHour = 9;

		Location location = new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United States of America");
		location.setEnabled(true);

		HourlyWeather hourlyWeather = new HourlyWeather().location(location).hourOfDay(10).temperature(13)
				.precipitation(70).status("Cloudy");
		HourlyWeather hourlyWeather2 = new HourlyWeather().location(location).hourOfDay(11).temperature(15)
				.precipitation(60).status("Sunny");
		List<HourlyWeather> list = new ArrayList<>();
		list.add(hourlyWeather);
		list.add(hourlyWeather2);

		Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
		when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(list);

		String expectedLocation = location.toString();
		mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.location", is(expectedLocation)))
				.andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10))).andDo(print());
	}

	@Test
	public void testGetByCodeShouldReturn400BadRequest() throws Exception {
		String locationCode = "DELHI_IN";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		mockMvc.perform(get(requestURI)).andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	public void testGetByCodeShouldReturn404NotFound() throws Exception {
		int currentHour = 12;
		String locationCode = "DELHI_IN";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		when(hourlyWeatherService.getByLocationCode(locationCode, currentHour))
				.thenThrow(LocationNotFoundException.class);
		mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
				.andExpect(status().isNotFound()).andDo(print());
	}

	@Test
	public void testGetByCodeShouldReturn204NoContent() throws Exception {
		int currentHour = 9;
		String locationCode = "DELHI_IN";
		String requestURI = END_POINT_PATH + "/" + locationCode;

		when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenReturn(Collections.emptyList());
		mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
				.andExpect(status().isNoContent()).andDo(print());
	}

	@Test
	public void testGetByCodeShouldReturn200OK() throws Exception {
		int currentHour = 9;
		String locationCode = "DELHI_IN";
		String requestURI = END_POINT_PATH + "/" + locationCode;

		Location location = new Location();
		location.setCode(locationCode);
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United States of America");

		HourlyWeather hourlyWeather = new HourlyWeather().location(location).hourOfDay(10).temperature(13)
				.precipitation(70).status("Cloudy");
		HourlyWeather hourlyWeather2 = new HourlyWeather().location(location).hourOfDay(11).temperature(15)
				.precipitation(60).status("Sunny");
		List<HourlyWeather> list = new ArrayList<>();
		list.add(hourlyWeather);
		list.add(hourlyWeather2);

		when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenReturn(list);

		mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour))).andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.location", is(location.toString())))
				.andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10))).andDo(print());
	}

	@Test
	public void testUpdateReturn400BadRequestBecauseNoData() throws Exception {
		String requestURI = END_POINT_PATH + "/NYC_USA";
		List<HourlyWeatherDTO> listDTO = Collections.emptyList();
		String requestBody = objectMapper.writeValueAsString(listDTO);
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0]", is("Hourly forecast data cannot be empty"))).andDo(print());

	}

	@Test
	public void testUpdateReturn400BadRequestBecauseInvalidData() throws Exception {
		String requestURI = END_POINT_PATH + "/NYC_USA";
		HourlyWeather hourlyWeather = new HourlyWeather().hourOfDay(10).temperature(133).precipitation(70)
				.status("Cloudy");
		HourlyWeather hourlyWeather2 = new HourlyWeather().hourOfDay(11).temperature(15).precipitation(60)
				.status("Sunny");
		List<HourlyWeather> listDTO = new ArrayList<>();
		listDTO.add(hourlyWeather);
		listDTO.add(hourlyWeather2);
		String requestBody = objectMapper.writeValueAsString(listDTO);
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0]", containsString("Temperature in (-50 -> 50) Celcius degree")))
				.andDo(print());

	}

	@Test
	public void testUpdateReturn404NotFound() throws Exception {
		String locationCode = "NYC_USA";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		HourlyWeather hourlyWeather = new HourlyWeather().hourOfDay(10).temperature(13).precipitation(70)
				.status("Cloudy");
		List<HourlyWeather> listDTO = new ArrayList<>();
		listDTO.add(hourlyWeather);
		String requestBody = objectMapper.writeValueAsString(listDTO);
		when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList()))
				.thenThrow(LocationNotFoundException.class);
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isNotFound()).andDo(print());

	}

	@Test
	public void testUpdateReturn200OK() throws Exception {
		String locationCode = "NYC_USA";
		String requestURI = END_POINT_PATH + "/" + locationCode;

		HourlyWeatherDTO dto = new HourlyWeatherDTO().hourOfDay(10).temperature(13).precipitation(70).status("Cloudy");
		HourlyWeatherDTO dto2 = new HourlyWeatherDTO().hourOfDay(11).temperature(15).precipitation(60).status("Sunny");
		List<HourlyWeatherDTO> listDTO = new ArrayList<>();
		listDTO.add(dto);
		listDTO.add(dto2);

		Location location = new Location();
		location.setCode(locationCode);
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United States of America");

		HourlyWeather forecast1 = new HourlyWeather().location(location).hourOfDay(10).temperature(13).precipitation(70)
				.status("Cloudy");
		HourlyWeather forecast2 = new HourlyWeather().location(location).hourOfDay(11).temperature(15).precipitation(60)
				.status("Sunny");
		List<HourlyWeather> hourlyForecast = new ArrayList<>();
		hourlyForecast.add(forecast1);
		hourlyForecast.add(forecast2);

		String requestBody = objectMapper.writeValueAsString(listDTO);
		when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList()))
				.thenReturn(hourlyForecast);
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.location", is(location.toString())))
				.andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
				.andDo(print());

	}
}
