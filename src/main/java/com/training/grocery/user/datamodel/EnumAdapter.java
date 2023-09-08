package com.training.grocery.user.datamodel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class EnumAdapter implements TypeAdapterFactory {

	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		Class<T> rawtype = (Class<T>) type.getRawType();
		if (!rawtype.isEnum())
			return null;
		final Map<String, T> toConst = new HashMap<String, T>();
		for (T constant : rawtype.getEnumConstants()) {
			toConst.put(constant.toString(), constant);
		}

		return new TypeAdapter<T>() {
			@Override
			public void write(JsonWriter out, T value) throws IOException {
				if (value == null) {
					out.nullValue();
				} else {
					out.value(value.toString());
				}
			}

			@Override
			public T read(JsonReader reader) throws IOException {
				if (reader.peek() == JsonToken.NULL) {
					reader.nextNull();
					return null;
				}
				return toConst.get(reader.nextString());
			}
		};
	}

}
