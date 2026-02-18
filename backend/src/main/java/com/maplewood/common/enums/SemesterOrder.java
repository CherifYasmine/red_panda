package com.maplewood.common.enums;

/**
 * Enum for Semester Order (within academic year)
 * Maps to database constraint: order_in_year/semester_order IN (1, 2)
 * 1 = Fall (1st semester)
 * 2 = Spring (2nd semester)
 */
public enum SemesterOrder {
    FIRST(1, "Fall"),
    SECOND(2, "Spring");

    private final int orderValue;
    private final String semesterName;

    SemesterOrder(int orderValue, String semesterName) {
        this.orderValue = orderValue;
        this.semesterName = semesterName;
    }

    public int getOrderValue() {
        return orderValue;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public static SemesterOrder fromOrderValue(int orderValue) {
        for (SemesterOrder order : SemesterOrder.values()) {
            if (order.orderValue == orderValue) {
                return order;
            }
        }
        throw new IllegalArgumentException("Invalid SemesterOrder: " + orderValue);
    }
}
