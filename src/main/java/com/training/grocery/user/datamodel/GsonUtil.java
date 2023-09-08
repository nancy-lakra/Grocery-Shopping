package com.training.grocery.user.datamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GsonUtil<T, U> {

	Logger log = LoggerFactory.getLogger(this.getClass());

	public T convert(T To, U From) {
		Gson gson = buildGson(To, From);
		String json = gson.toJson(From);
		T res = (T) gson.fromJson(json, To.getClass());
		return res;
	}

	private Gson buildGson(T to, U from) {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapterFactory(new EnumAdapter());
		Gson gson = builder.create();
		return gson;
	}
}
