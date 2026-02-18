package com.maplewood.common.converter.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.maplewood.common.enums.SemesterName;

/**
 * Spring Web Converter for SemesterName enum
 * Converts string query parameters to SemesterName enum values
 * Used for @RequestParam conversion
 */
@Component
public class StringToSemesterNameConverter implements Converter<String, SemesterName> {
    
    @Override
    public SemesterName convert(String source) {
        return SemesterName.fromDbValue(source);
    }
}
