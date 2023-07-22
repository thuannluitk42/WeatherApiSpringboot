package com.skyapi.weatherforecast.location;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.common.Location;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/locations")
public class LocationApiController {

	private LocationService service;
	private ModelMapper modelMapper;

	public LocationApiController(LocationService service, ModelMapper modelMapper) {
		this.service = service;
		this.modelMapper = modelMapper;
	}

	@PostMapping
	public ResponseEntity<LocationDTO> addLocation(@RequestBody @Valid LocationDTO location) {
		Location addedLocation = service.add(dto2Entity(location));
		URI uri = URI.create("/v1/locations/" + location.getCode());
		return ResponseEntity.created(uri).body(entity2DTO(addedLocation));
	}

	@GetMapping
	public ResponseEntity<?> listLocations() {
		List<Location> locations = service.list();
		if (locations.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(listEntity2ListDTO(locations));
	}

	@GetMapping("/{code}")
	public ResponseEntity<?> getLocation(@PathVariable("code") String code) {
		Location location = service.get(code);
		return ResponseEntity.ok(entity2DTO(location));
	}

	@PutMapping
	public ResponseEntity<?> updateLocation(@RequestBody @Valid LocationDTO location) {
		Location locationDB = service.update(dto2Entity(location));
		return ResponseEntity.ok(entity2DTO(locationDB));

	}

	@DeleteMapping("/{code}")
	public ResponseEntity<?> deleteLocation(@PathVariable("code") String code) {
		service.delete(code);
		return ResponseEntity.noContent().build();
	}

	private List<LocationDTO> listEntity2ListDTO(List<Location> listEntity) {
		return listEntity.stream().map(enity -> entity2DTO(enity)).collect(Collectors.toList());
	}

	private LocationDTO entity2DTO(Location entity) {
		return modelMapper.map(entity, LocationDTO.class);
	}

	private Location dto2Entity(LocationDTO locationDTO) {
		return modelMapper.map(locationDTO, Location.class);
	}

}
