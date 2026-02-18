package com.maplewood.common.converter;

import com.maplewood.common.enums.DayOfWeek;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Converter for DayOfWeek enum
 * Handles mapping between Java enum constants (UPPERCASE) and database values (integers 1-5)
 */
@Converter(autoApply = true)
public class DayOfWeekConverter implements AttributeConverter<DayOfWeek, Integer> {

    @Override
    public Integer convertToDatabaseColumn(DayOfWeek attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDayValue();
    }

    @Override
    public DayOfWeek convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return DayOfWeek.fromDayValue(dbData);
    }
}
