package com.training.grocery.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;


@Converter(autoApply = true)
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

	@Override
	public Timestamp convertToDatabaseColumn(LocalDateTime localDate) {
		if (localDate != null)
			return Timestamp.valueOf(localDate);
		return null;
	}

	@Override
	public LocalDateTime convertToEntityAttribute(Timestamp dbData) {
		if (dbData != null)
			return dbData.toLocalDateTime();
		return null;
	}
}
