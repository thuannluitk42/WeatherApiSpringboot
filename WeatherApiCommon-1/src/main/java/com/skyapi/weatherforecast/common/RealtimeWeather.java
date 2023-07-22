package com.skyapi.weatherforecast.common;

import java.util.Date;
import java.util.Objects;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "realtime_weather")
public class RealtimeWeather {
	@Id
	@Column(name = "location_code")
	@JsonIgnore
	private String locationCode;
	@Range(min = -50, max = 50, message = "Temperature in (-50 -> 50) Celcius degree")
	private int temperature;
	@Range(min = 0, max = 100, message = "Humidity in (0 -> 100) percentage")
	private int humidity;
	@Range(min = 0, max = 100, message = "Precipipation in (0 -> 100) percentage")
	private int precipipation;
	@Range(min = 0, max = 200, message = "Wind Speed in (0 -> 200) km/h")
	@JsonProperty("wind_speed")
	private int windSpeed;
	@Column(length = 50)
	@NotBlank(message = "Status must not empty")
	@Length(min = 3, max = 50, message = "Status must be in 3 to 50 character")
	private String status;
	@JsonProperty("last_updated")
	@JsonIgnore
	private Date lastUpdated;
	@OneToOne
	@JoinColumn(name = "location_code")
	@MapsId
	@JsonIgnore
	private Location location;

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public int getTemperature() {
		return temperature;
	}

	public int getPrecipipation() {
		return precipipation;
	}

	public void setPrecipipation(int precipipation) {
		this.precipipation = precipipation;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public int getHumidity() {
		return humidity;
	}

	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}

	public int getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(int windSpeed) {
		this.windSpeed = windSpeed;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.locationCode = location.getCode();
		this.location = location;
	}

	@Override
	public int hashCode() {
		return Objects.hash(locationCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RealtimeWeather other = (RealtimeWeather) obj;
		return Objects.equals(locationCode, other.locationCode);
	}

}
