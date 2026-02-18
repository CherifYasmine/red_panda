package com.maplewood.common.converter;

import com.maplewood.common.enums.SemesterName;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Converter for SemesterName enum
 * Handles mapping between Java enum constants (UPPERCASE) and database values (titlecase)
 */
@Converter(autoApply = true)
public class SemesterNameConverter implements AttributeConverter<SemesterName, String> {

    @Override
    public String convertToDatabaseColumn(SemesterName attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDbValue();
    }

    @Override
    public SemesterName convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return SemesterName.fromDbValue(dbData);
    }
}
