package com.maplewood.school.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maplewood.common.dto.ClassroomDTO;
import com.maplewood.common.mapper.ClassroomMapper;
import com.maplewood.school.entity.Classroom;
import com.maplewood.school.service.ClassroomService;

/**
 * REST controller for Classroom endpoints
 * Provides CRUD operations for classrooms
 */
@RestController
@RequestMapping("/api/v1/classrooms")
@CrossOrigin(origins = "*")
public class ClassroomController {
    
    @Autowired
    private ClassroomService classroomService;
    
    /**
     * GET all classrooms
     */
    @GetMapping
    public ResponseEntity<List<ClassroomDTO>> getAllClassrooms() {
        return ResponseEntity.ok(classroomService.getAllClassrooms()
            .stream()
            .map(ClassroomMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * GET classroom by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClassroomDTO> getClassroomById(@PathVariable Long id) {
        return ResponseEntity.ok(ClassroomMapper.toDTO(classroomService.getClassroomById(id)));
    }
    
    /**
     * GET classroom by name
     */
    @GetMapping("/search/name")
    public ResponseEntity<ClassroomDTO> getClassroomByName(@RequestParam String name) {
        return ResponseEntity.ok(ClassroomMapper.toDTO(classroomService.getClassroomByName(name)));
    }
    
    /**
     * GET classrooms by room type
     */
    @GetMapping("/room-type/{roomTypeId}")
    public ResponseEntity<List<ClassroomDTO>> getClassroomsByRoomType(@PathVariable Long roomTypeId) {
        return ResponseEntity.ok(classroomService.getClassroomsByRoomType(roomTypeId)
            .stream()
            .map(ClassroomMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * GET classrooms by floor
     */
    @GetMapping("/floor/{floor}")
    public ResponseEntity<List<ClassroomDTO>> getClassroomsByFloor(@PathVariable Integer floor) {
        return ResponseEntity.ok(classroomService.getClassroomsByFloor(floor)
            .stream()
            .map(ClassroomMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * POST create new classroom
     */
    @PostMapping
    public ResponseEntity<ClassroomDTO> createClassroom(@RequestBody ClassroomDTO classroomDTO) {
        Classroom entity = ClassroomMapper.toEntity(classroomDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClassroomMapper.toDTO(classroomService.createClassroom(entity)));
    }
    
    /**
     * PUT update classroom
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClassroomDTO> updateClassroom(@PathVariable Long id, @RequestBody ClassroomDTO classroomDTO) {
        Classroom entity = ClassroomMapper.toEntity(classroomDTO);
        return ResponseEntity.ok(ClassroomMapper.toDTO(classroomService.updateClassroom(id, entity)));
    }
    
    /**
     * DELETE classroom
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id) {
        classroomService.deleteClassroom(id);
        return ResponseEntity.noContent().build();
    }
}
