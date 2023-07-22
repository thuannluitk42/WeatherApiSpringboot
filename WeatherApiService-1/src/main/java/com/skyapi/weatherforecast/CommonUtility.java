package com.skyapi.weatherforecast;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtility {
	private static Logger LOGGER = LoggerFactory.getLogger(CommonUtility.class);

	public static String getIPAddress(HttpServletRequest httpServletRequest) {
		String ip = httpServletRequest.getHeader("X-FORWARED-FOR");
		//String ip = "210.138.184.59";
		if (ip == null || ip.isEmpty()) {
			ip = httpServletRequest.getRemoteAddr();
		}

		LOGGER.info("Client's IP Address: " + ip);
		return ip;
	}
}
