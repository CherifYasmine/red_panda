package com.maplewood.common.enums;

/**
 * Enum for Course History Status
 * Maps to database constraint: status IN ('passed', 'failed')
 */
public enum CourseHistoryStatus {
    PASSED("passed"),
    FAILED("failed");

    private final String dbValue;

    CourseHistoryStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static CourseHistoryStatus fromDbValue(String dbValue) {
        for (CourseHistoryStatus status : CourseHistoryStatus.values()) {
            if (status.dbValue.equalsIgnoreCase(dbValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid CourseHistoryStatus: " + dbValue);
    }
}
