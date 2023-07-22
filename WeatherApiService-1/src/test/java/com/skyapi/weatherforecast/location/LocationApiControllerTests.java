package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.Location;

@WebMvcTest(LocationApiController.class)
public class LocationApiControllerTests {
	private static final String END_POINT_PATH = "/v1/locations";

	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper mapper;
	@MockBean
	LocationService service;

	@Test
	public void testAddLocationWith400BadRequest() throws Exception {
		Location location = new Location();

		String bodyContent = mapper.writeValueAsString(location);
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
				.andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	public void testAddReturn201Created() throws Exception {
		Location location = new Location();
		location.setCode("JP");
		location.setCityName("Japan");
		location.setRegionName("Japan");
		location.setCountryCode("JP");
		location.setCountryName("Japan");
		location.setEnabled(true);

		Mockito.when(service.add(location)).thenReturn(location);
		String bodyContent = mapper.writeValueAsString(location);

		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
				.andExpect(status().isCreated()).andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.code", is("JP"))).andExpect(jsonPath("$.city_name", is("Japan")))
				.andExpect(header().string("Location", "/v1/locations/JP")).andDo(print());

	}

	@Test
	public void testGetListLocation204NoContent() throws Exception {
		Mockito.when(service.list()).thenReturn(Collections.emptyList());
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNoContent()).andDo(print());
	}

	@Test
	public void testGetListLocationReturn200() throws Exception {
		Location location = new Location();
		location.setCode("JP");
		location.setCityName("Janpan");
		location.setRegionName("Japan");
		location.setCountryCode("JP");
		location.setCountryName("Japan");
		location.setEnabled(true);

		Location location2 = new Location();
		location2.setCode("USA");
		location2.setCityName("New York");
		location2.setRegionName("New York");
		location2.setCountryCode("USA");
		location2.setCountryName("New York");
		location2.setEnabled(true);

		Location location3 = new Location();
		location3.setCode("Switzerland");
		location3.setCityName("Switzerland");
		location3.setRegionName("Switzerland");
		location3.setCountryCode("Switzerland");
		location3.setCountryName("Switzerland");
		location3.setEnabled(true);

		Mockito.when(service.list()).thenReturn(List.of(location, location2, location3));

		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isOk())
				.andExpect(content().contentType("application/json")).andExpect(jsonPath("$[0].code", is("JP")))
				.andExpect(jsonPath("$[0].city_name", is("Janpan"))).andExpect(jsonPath("$[1].code", is("USA")))
				.andExpect(jsonPath("$[1].city_name", is("New York"))).andDo(print());
	}

	@Test
	public void testGetLocationReturn405MethodNotAllow() throws Exception {
		String requestURI = END_POINT_PATH + "/onetwothree";
		mockMvc.perform(post(requestURI)).andExpect(status().isMethodNotAllowed()).andDo(print());
	}

	@Test
	public void testGetLocationReturn404NotFound() throws Exception {
		String requestURI = END_POINT_PATH + "/onetwothree";
		mockMvc.perform(get(requestURI)).andExpect(status().isNotFound()).andDo(print());
	}

	@Test
	public void testGetLocationReturn200OK() throws Exception {
		String code = "DL";
		String requestURI = END_POINT_PATH + "/" + code;
		mockMvc.perform(get(requestURI)).andExpect(status().isOk()).andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.code", is(code))).andExpect(jsonPath("$.city_name", is("Dallats"))).andDo(print());
	}

	@Test
	public void testUpdate404NotFound() throws Exception {
		LocationDTO location = new LocationDTO();
		location.setCode("JP");
		location.setCityName("Janpan");
		location.setRegionName("Japan");
		location.setCountryCode("JP");
		location.setCountryName("Japan");
		location.setEnabled(true);

		LocationNotFoundException exception = new LocationNotFoundException(location.getCityName());

		Mockito.when(service.update(Mockito.any())).thenThrow(exception);
		String bodyContent = mapper.writeValueAsString(location);
		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.errors[0]", is(exception.getMessage())))
				.andDo(print());

	}

	@Test
	public void testUpdate400BadRequest() throws Exception {
		Location location = new Location();
		location.setCode("");
		location.setCityName("Janpan");
		location.setRegionName("Japan");
		location.setCountryCode("JP");
		location.setCountryName("Japan");
		location.setEnabled(true);

		Mockito.when(service.update(location)).thenReturn(location);
		String bodyContent = mapper.writeValueAsString(location);
		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
				.andExpect(status().isBadRequest()).andDo(print());

	}

	@Test
	public void testUpdateReturn201OK() throws Exception {
		Location location = new Location();
		location.setCode("JP2");
		location.setCityName("Japan3");
		location.setRegionName("Japan2");
		location.setCountryCode("JP");
		location.setCountryName("Japan2");
		location.setEnabled(true);

		Mockito.when(service.update(location)).thenReturn(location);
		String bodyContent = mapper.writeValueAsString(location);

		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
				.andExpect(status().isCreated()).andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.code", is("JP2"))).andExpect(jsonPath("$.city_name", is("Japan2")))
				.andExpect(header().string("Location", "/v1/locations/JP2")).andDo(print());

	}

	@Test
	public void testDeleteReturn404NotFound() throws Exception {
		String code = "JP";
		String requestURI = END_POINT_PATH + "/" + code;
		LocationNotFoundException exception = new LocationNotFoundException(code);
		Mockito.doThrow(exception).when(service).delete(code);

		mockMvc.perform(delete(requestURI)).andExpect(status().isNotFound())
		.andExpect(jsonPath("$.errors[0]", is(exception.getMessage())))
		.andDo(print());

	}

	@Test
	public void testDeleteReturn204NoContent() throws Exception {
		String code = "JP";
		String requestURI = END_POINT_PATH + "/" + code;

		Mockito.doNothing().when(service).delete(code);

		mockMvc.perform(delete(requestURI)).andExpect(status().isNoContent()).andDo(print());

	}

	@Test
	public void testValidateRequestBodyLocationCode() throws Exception {
		Location location = new Location();
		location.setCityName("Japan1");
		location.setRegionName("Japan1");
		location.setCountryCode("JP1");
		location.setCountryName("Japan1");
		location.setEnabled(true);

		String bodyContent = mapper.writeValueAsString(location);

		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
				.andExpect(status().isBadRequest()).andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.errors[0]", is("Location code cannot null ok"))).andDo(print());

	}

	@Test
	public void testValidateRequestBodyLocationCodeLength() throws Exception {
		Location location = new Location();
		location.setCode("");
		location.setCityName("Japan1");
		location.setRegionName("Japan1");
		location.setCountryCode("JP1");
		location.setCountryName("Japan1");
		location.setEnabled(true);

		String bodyContent = mapper.writeValueAsString(location);

		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
				.andExpect(status().isBadRequest()).andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.errors[0]", is("length location code must be 2->12 character."))).andDo(print());

	}

	@Test
	public void testValidateRequestBodyAllFieldsInvalid() throws Exception {
		Location location = new Location();

		String bodyContent = mapper.writeValueAsString(location);

		MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
				.andExpect(status().isBadRequest()).andExpect(content().contentType("application/json")).andDo(print())
				.andReturn();
		String responseBody = mvcResult.getResponse().getContentAsString();
		assertThat(responseBody).contains("Location code cannot null ok");

	}
}
