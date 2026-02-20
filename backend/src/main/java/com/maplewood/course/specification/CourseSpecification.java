package com.maplewood.course.specification;

import org.springframework.data.jpa.domain.Specification;

import com.maplewood.common.enums.CourseType;
import com.maplewood.course.entity.Course;

/**
 * Course Specifications for dynamic filtering
 */
public class CourseSpecification {
    
    /**
     * Filter by specialization ID
     */
    public static Specification<Course> bySpecialization(Long specializationId) {
        return (root, query, criteriaBuilder) ->
            specializationId == null ? 
            criteriaBuilder.conjunction() :
            criteriaBuilder.equal(root.get("specialization").get("id"), specializationId);
    }
    
    /**
     * Filter by course type (CORE or ELECTIVE)
     */
    public static Specification<Course> byType(CourseType type) {
        return (root, query, criteriaBuilder) ->
            type == null ?
            criteriaBuilder.conjunction() :
            criteriaBuilder.equal(root.get("courseType"), type);
    }
    
    /**
     * Filter by grade level (courses available for this grade)
     */
    public static Specification<Course> byGradeLevel(Integer gradeLevel) {
        return (root, query, criteriaBuilder) ->
            gradeLevel == null ?
            criteriaBuilder.conjunction() :
            criteriaBuilder.and(
                criteriaBuilder.lessThanOrEqualTo(root.get("gradeLevelMin"), gradeLevel),
                criteriaBuilder.greaterThanOrEqualTo(root.get("gradeLevelMax"), gradeLevel)
            );
    }
    
    /**
     * Filter by semester order (1 for Fall, 2 for Spring)
     */
    public static Specification<Course> bySemesterOrder(Integer semesterOrder) {
        return (root, query, criteriaBuilder) ->
            semesterOrder == null ?
            criteriaBuilder.conjunction() :
            criteriaBuilder.equal(root.get("semesterOrder"), semesterOrder);
    }
    
    /**
     * Combine all filters using AND logic
     */
    public static Specification<Course> withFilters(
            Long specialization,
            CourseType type,
            Integer gradeLevel,
            Integer semesterOrder) {
        
        return Specification
            .where(bySpecialization(specialization))
            .and(byType(type))
            .and(byGradeLevel(gradeLevel))
            .and(bySemesterOrder(semesterOrder));
    }
}
