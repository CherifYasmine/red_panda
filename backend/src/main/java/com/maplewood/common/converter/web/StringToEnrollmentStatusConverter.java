package com.maplewood.common.converter.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.maplewood.common.enums.EnrollmentStatus;

/**
 * Spring Web Converter for EnrollmentStatus enum
 * Converts string query parameters to EnrollmentStatus enum values
 * Used for @RequestParam conversion
 */
@Component
public class StringToEnrollmentStatusConverter implements Converter<String, EnrollmentStatus> {
    
    @Override
    public EnrollmentStatus convert(String source) {
        return EnrollmentStatus.fromDbValue(source);
    }
}
