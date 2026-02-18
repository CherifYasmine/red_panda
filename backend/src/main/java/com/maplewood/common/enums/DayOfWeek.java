package com.maplewood.common.enums;

/**
 * Enum for Day of Week
 * Used for course_section_meetings: day_of_week (1-5 for Monday-Friday)
 * Common database convention where 1=Monday, 5=Friday
 */
public enum DayOfWeek {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5);

    private final int dayValue;

    DayOfWeek(int dayValue) {
        this.dayValue = dayValue;
    }

    public int getDayValue() {
        return dayValue;
    }

    public String getDayName() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }

    public static DayOfWeek fromDayValue(int dayValue) {
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day.dayValue == dayValue) {
                return day;
            }
        }
        throw new IllegalArgumentException("Invalid DayOfWeek: " + dayValue + ". Must be 1-5 (Monday-Friday)");
    }
}
