package com.maplewood.common.enums;

/**
 * Enum for Student Status
 * Maps to database constraint: status IN ('active', 'inactive', 'graduated')
 */
public enum StudentStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    GRADUATED("graduated");

    private final String dbValue;

    StudentStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static StudentStatus fromDbValue(String dbValue) {
        for (StudentStatus status : StudentStatus.values()) {
            if (status.dbValue.equalsIgnoreCase(dbValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid StudentStatus: " + dbValue);
    }
}
