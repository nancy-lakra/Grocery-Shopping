package com.training.grocery.restapi;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Restapi {

	public static void main(String[] args) throws IOException {
		URL url = new URL("https://api.nasa.gov/planetary/apod?date=2021-07-06&api_key=DEMO_KEY");

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestProperty("accept", "application/json");

		InputStream responseStream = connection.getInputStream();

		ObjectMapper mapper = new ObjectMapper();
		Responseclass picDetails = mapper.readValue(responseStream, Responseclass.class);

		System.out.println("copyright= " + picDetails.copyright);
		System.out.println("date= " + picDetails.date);
		System.out.println("explanation= " + picDetails.explanation);
		System.out.println("hdUrl= " + picDetails.hdUrl);
		System.out.println("mediaType= " + picDetails.mediaType);
		System.out.println("serviceVersion= " + picDetails.serviceVersion);
		System.out.println("title= " + picDetails.title);
		System.out.println("url= " + picDetails.url);
	}
}

