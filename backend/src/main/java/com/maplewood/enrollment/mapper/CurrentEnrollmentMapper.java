package com.maplewood.enrollment.mapper;

import com.maplewood.common.dto.CourseSectionDTO;
import com.maplewood.common.dto.CreateEnrollmentDTO;
import com.maplewood.common.dto.EnrollmentDTO;
import com.maplewood.common.dto.StudentDTO;
import com.maplewood.common.dto.UpdateEnrollmentDTO;
import com.maplewood.common.enums.EnrollmentStatus;
import com.maplewood.common.mapper.CourseSectionMapper;
import com.maplewood.course.entity.CourseSection;
import com.maplewood.enrollment.entity.CurrentEnrollment;
import com.maplewood.student.entity.Student;

/**
 * Mapper for CurrentEnrollment entity <-> DTOs
 * Handles conversion between database entities and API DTOs
 */
public class CurrentEnrollmentMapper {
    
    /**
     * Convert CurrentEnrollment entity to EnrollmentDTO
     */
    public static EnrollmentDTO toDTO(CurrentEnrollment enrollment) {
        if (enrollment == null) {
            return null;
        }
        
        // Convert Student to StudentDTO
        StudentDTO studentDTO = new StudentDTO();
        if (enrollment.getStudent() != null) {
            studentDTO.setId(enrollment.getStudent().getId());
            studentDTO.setFirstName(enrollment.getStudent().getFirstName());
            studentDTO.setLastName(enrollment.getStudent().getLastName());
            studentDTO.setEmail(enrollment.getStudent().getEmail());
            studentDTO.setGradeLevel(enrollment.getStudent().getGradeLevel());
        }
        
        CourseSectionDTO sectionDTO = CourseSectionMapper.toDTO(enrollment.getCourseSection());
        
        return new EnrollmentDTO(
            enrollment.getId(),
            studentDTO,
            sectionDTO,
            enrollment.getGrade(),
            enrollment.getStatus() != null ? enrollment.getStatus().toString() : "ENROLLED",
            enrollment.getCreatedAt(),
            enrollment.getUpdatedAt()
        );
    }
    
    /**
     * Convert CreateEnrollmentDTO to CurrentEnrollment entity
     */
    public static CurrentEnrollment toEntityFromCreate(CreateEnrollmentDTO dto, Student student, CourseSection section) {
        CurrentEnrollment enrollment = new CurrentEnrollment();
        enrollment.setStudent(student);
        enrollment.setCourseSection(section);
        enrollment.setSemester(section.getSemester());
        enrollment.setStatus(EnrollmentStatus.ENROLLED);  // Default status
        return enrollment;
    }
    
    /**
     * Update CurrentEnrollment entity from UpdateEnrollmentDTO
     */
    public static void updateFromDTO(UpdateEnrollmentDTO dto, CurrentEnrollment enrollment) {
        if (dto.grade() != null) {
            enrollment.setGrade(dto.grade());
        }
    }
}
