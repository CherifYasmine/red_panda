package com.maplewood.common.enums;

/**
 * Enum for Course Type
 * Maps to database constraint: course_type IN ('core', 'elective')
 */
public enum CourseType {
    CORE("core"),
    ELECTIVE("elective");

    private final String dbValue;

    CourseType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static CourseType fromDbValue(String dbValue) {
        for (CourseType type : CourseType.values()) {
            if (type.dbValue.equalsIgnoreCase(dbValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid CourseType: " + dbValue);
    }
}
