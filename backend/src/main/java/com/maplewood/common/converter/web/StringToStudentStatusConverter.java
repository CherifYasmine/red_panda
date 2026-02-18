package com.maplewood.common.converter.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.maplewood.common.enums.StudentStatus;

/**
 * Spring Web Converter for StudentStatus enum
 * Converts string query parameters to StudentStatus enum values
 * Used for @RequestParam conversion
 */
@Component
public class StringToStudentStatusConverter implements Converter<String, StudentStatus> {
    
    @Override
    public StudentStatus convert(String source) {
        return StudentStatus.fromDbValue(source);
    }
}
