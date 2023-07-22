package com.skyapi.weatherforecast.location;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.Location;

@Service
@org.springframework.transaction.annotation.Transactional
public class LocationService {
	private LocationRepository repo;

	public LocationService(LocationRepository repo) {
		super();
		this.repo = repo;
	}

	public Location add(Location location) {
		return repo.save(location);
	}

	public List<Location> list() {
		return repo.findUntrashed();
	}

	public Location get(String code) {
		Location location = repo.findByCode(code);
		if (location == null) {
			throw new LocationNotFoundException(code);
		}
		return location;
	}

	public Location update(Location location) {
		String code = location.getCode();
		Location locationDB = repo.findByCode(code);

		if (locationDB == null) {
			throw new LocationNotFoundException(code);
		}

		locationDB.setCityName(location.getCityName());
		locationDB.setRegionName(location.getRegionName());
		locationDB.setCountryCode(location.getCountryCode());
		locationDB.setCountryName(location.getCountryName());
		locationDB.setEnabled(location.isEnabled());

		return repo.save(locationDB);
	}

	public void delete(String code) {
		Location location = repo.findByCode(code);
		if (location == null) {
			throw new LocationNotFoundException(code);
		}
		repo.trashByCode(code);
	}

}
