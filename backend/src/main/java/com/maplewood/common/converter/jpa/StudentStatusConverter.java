package com.maplewood.common.converter.jpa;

import com.maplewood.common.enums.StudentStatus;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Converter for StudentStatus enum
 * Handles mapping between Java enum constants (UPPERCASE) and database values (titlecase)
 */
@Converter(autoApply = true)
public class StudentStatusConverter implements AttributeConverter<StudentStatus, String> {

    @Override
    public String convertToDatabaseColumn(StudentStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDbValue();
    }

    @Override
    public StudentStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return StudentStatus.fromDbValue(dbData);
    }
}
