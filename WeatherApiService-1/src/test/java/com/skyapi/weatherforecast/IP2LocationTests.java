package com.skyapi.weatherforecast;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;

public class IP2LocationTests {
	private String DBPath = "ip2locationdb/IP2LOCATION-LITE-DB3.BIN";

	@Test
	public void testInvalidIp() throws IOException {
		IP2Location ip2Location = new IP2Location();
		ip2Location.Open(DBPath);

		String ipAddress = "210.138.184.59";
		IPResult ipResult = ip2Location.IPQuery(ipAddress);

		assertThat(ipResult.getStatus()).isEqualTo("OK");
		System.out.println(ipResult);
	}

//	@Test
//	public void binaryToTextConverter() {
//		String inputFile = DBPath; // Replace with the path to your binary file
//		String outputFile = "ip2locationdb/output.txt"; // Replace with the desired path for the text file
//
//		try {
//			// Reading binary file
//			FileInputStream fis = new FileInputStream(inputFile);
//			InputStreamReader isr = new InputStreamReader(fis);
//			BufferedReader br = new BufferedReader(isr);
//
//			// Writing text file
//			FileWriter fw = new FileWriter(outputFile);
//			BufferedWriter bw = new BufferedWriter(fw);
//
//			String line;
//			while ((line = br.readLine()) != null) {
//				// Write each line to the text file
//				bw.write(line);
//				bw.newLine(); // Add a new line after each line in the text file
//			}
//
//			// Close the resources
//			br.close();
//			bw.close();
//			System.out.println("File conversion completed successfully.");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
