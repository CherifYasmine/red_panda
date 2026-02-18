package com.maplewood.common.enums;

/**
 * Enum for Current Enrollment Status
 * Maps to database: status IN ('enrolled', 'withdrawn')
 */
public enum EnrollmentStatus {
    ENROLLED("enrolled"),
    WITHDRAWN("withdrawn");

    private final String dbValue;

    EnrollmentStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static EnrollmentStatus fromDbValue(String dbValue) {
        for (EnrollmentStatus status : EnrollmentStatus.values()) {
            if (status.dbValue.equalsIgnoreCase(dbValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid EnrollmentStatus: " + dbValue);
    }
}
