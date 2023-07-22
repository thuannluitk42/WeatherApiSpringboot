package com.skyapi.weatherforecast.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "locations")
public class Location {

	@Id
	@Column(length = 12, nullable = false, unique = true)
	@NotNull(message = "Location code cannot null ok")
	@Length(min = 2, max = 12, message = "length location code must be 2->12 character.")
	private String code;

	@Column(length = 128, nullable = false)
	@JsonProperty("city_name")
	@NotNull(message = "City name cannot blank Ok")
	@Length(min = 3, max = 128, message = "length city name must be 3->128 character.")
	private String cityName;

	@Column(length = 128)
	@JsonProperty("region_name")
	@Length(min = 3, max = 128, message = "length region name must be 3->128 character.")
	private String regionName;

	@Column(length = 64, nullable = false)
	@JsonProperty("country_name")
	@NotNull(message = "Country name cannot blank Ok")
	@Length(min = 3, max = 64, message = "length country name must be 3->64 character.")
	private String countryName;

	@Column(length = 2, nullable = false)
	@JsonProperty("country_code")
	@NotNull(message = "Country code cannot null Ok")
	@Length(min = 2, max = 2, message = "length country code must be 2 character.")
	private String countryCode;
	@JsonIgnore
	private boolean enabled;
	@JsonIgnore
	private boolean trashed;

	@OneToOne(mappedBy = "location", cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn
	@JsonIgnore
	// bo di nhung thong tin load sau cung de api ko bi loi,
	// hoac lo thong tin nhay cam
	private RealtimeWeather realtimeWeather;

	@OneToMany(mappedBy = "id.location", cascade = CascadeType.ALL,orphanRemoval = true)
	private List<HourlyWeather> listHourlyWeather = new ArrayList<>();

	public String getCode() {
		return code;
	}

	public RealtimeWeather getRealtimeWeather() {
		return realtimeWeather;
	}

	public void setRealtimeWeather(RealtimeWeather realtimeWeather) {
		this.realtimeWeather = realtimeWeather;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isTrashed() {
		return trashed;
	}

	public void setTrashed(boolean trashed) {
		this.trashed = trashed;
	}

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		return Objects.equals(code, other.code);
	}

	public Location(String cityName,String regionName,String countryName,String countryCode) {
		this.cityName = cityName;
		this.regionName = regionName;
		this.countryName = countryName;
		this.countryCode = countryCode;
	}

	public Location() {
	}

	@Override
	public String toString() {
		return cityName + ", " + (regionName != null ? regionName + ", " : "") + countryName;
	}

	public List<HourlyWeather> getListHourlyWeather() {
		return listHourlyWeather;
	}

	public void setListHourlyWeather(List<HourlyWeather> listHourlyWeather) {
		this.listHourlyWeather = listHourlyWeather;
	}

	public Location code(String code) {
		setCode(code);
		return this;
	}

}
