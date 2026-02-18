package com.maplewood.common.converter;

import com.maplewood.common.enums.CourseHistoryStatus;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Converter for CourseHistoryStatus enum
 * Handles mapping between Java enum constants (UPPERCASE) and database values (titlecase)
 */
@Converter(autoApply = true)
public class CourseHistoryStatusConverter implements AttributeConverter<CourseHistoryStatus, String> {

    @Override
    public String convertToDatabaseColumn(CourseHistoryStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDbValue();
    }

    @Override
    public CourseHistoryStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return CourseHistoryStatus.fromDbValue(dbData);
    }
}
