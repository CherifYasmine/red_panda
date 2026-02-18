package com.maplewood.common.converter.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.maplewood.common.enums.CourseType;

/**
 * Spring Web Converter for CourseType enum
 * Converts string query parameters to CourseType enum values
 * Used for @RequestParam conversion
 */
@Component
public class StringToCourseTypeConverter implements Converter<String, CourseType> {
    
    @Override
    public CourseType convert(String source) {
        return CourseType.fromDbValue(source);
    }
}
