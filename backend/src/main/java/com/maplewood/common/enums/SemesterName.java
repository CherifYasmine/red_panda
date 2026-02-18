package com.maplewood.common.enums;

/**
 * Enum for Semester Name
 * Maps to database: name IN ('Fall', 'Spring')
 * Also maps to order_in_year: 1=Fall, 2=Spring
 */
public enum SemesterName {
    FALL("Fall", 1),
    SPRING("Spring", 2);

    private final String dbValue;
    private final int orderInYear; // 1 for Fall, 2 for Spring

    SemesterName(String dbValue, int orderInYear) {
        this.dbValue = dbValue;
        this.orderInYear = orderInYear;
    }

    public String getDbValue() {
        return dbValue;
    }

    public int getOrderInYear() {
        return orderInYear;
    }

    public static SemesterName fromDbValue(String dbValue) {
        for (SemesterName name : SemesterName.values()) {
            if (name.dbValue.equalsIgnoreCase(dbValue)) {
                return name;
            }
        }
        throw new IllegalArgumentException("Invalid SemesterName: " + dbValue);
    }

    public static SemesterName fromOrderInYear(int orderInYear) {
        for (SemesterName name : SemesterName.values()) {
            if (name.orderInYear == orderInYear) {
                return name;
            }
        }
        throw new IllegalArgumentException("Invalid orderInYear: " + orderInYear);
    }
}
