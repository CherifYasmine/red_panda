package com.maplewood.common.converter.jpa;

import com.maplewood.common.enums.CourseType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Converter for CourseType enum
 * Handles mapping between Java enum constants (UPPERCASE) and database values (titlecase)
 */
@Converter(autoApply = true)
public class CourseTypeConverter implements AttributeConverter<CourseType, String> {

    @Override
    public String convertToDatabaseColumn(CourseType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDbValue();
    }

    @Override
    public CourseType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return CourseType.fromDbValue(dbData);
    }
}
