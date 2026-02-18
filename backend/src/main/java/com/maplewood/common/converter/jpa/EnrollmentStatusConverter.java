package com.maplewood.common.converter.jpa;

import com.maplewood.common.enums.EnrollmentStatus;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Converter for EnrollmentStatus enum
 * Handles mapping between Java enum constants (UPPERCASE) and database values (lowercase)
 */
@Converter(autoApply = true)
public class EnrollmentStatusConverter implements AttributeConverter<EnrollmentStatus, String> {

    @Override
    public String convertToDatabaseColumn(EnrollmentStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDbValue();
    }

    @Override
    public EnrollmentStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return EnrollmentStatus.fromDbValue(dbData);
    }
}
