package com.maplewood.common.converter.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.maplewood.common.enums.CourseHistoryStatus;

/**
 * Spring Web Converter for CourseHistoryStatus enum
 * Converts string query parameters to CourseHistoryStatus enum values
 * Used for @RequestParam conversion
 */
@Component
public class StringToCourseHistoryStatusConverter implements Converter<String, CourseHistoryStatus> {
    
    @Override
    public CourseHistoryStatus convert(String source) {
        return CourseHistoryStatus.fromDbValue(source);
    }
}
