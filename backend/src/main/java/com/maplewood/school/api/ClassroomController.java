package com.maplewood.school.api;

import java.util.List;

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
    public ResponseEntity<List<Classroom>> getAllClassrooms() {
        return ResponseEntity.ok(classroomService.getAllClassrooms());
    }
    
    /**
     * GET classroom by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable Long id) {
        return ResponseEntity.ok(classroomService.getClassroomById(id));
    }
    
    /**
     * GET classroom by name
     */
    @GetMapping("/search/name")
    public ResponseEntity<Classroom> getClassroomByName(@RequestParam String name) {
        return ResponseEntity.ok(classroomService.getClassroomByName(name));
    }
    
    /**
     * GET classrooms by room type
     */
    @GetMapping("/room-type/{roomTypeId}")
    public ResponseEntity<List<Classroom>> getClassroomsByRoomType(@PathVariable Long roomTypeId) {
        return ResponseEntity.ok(classroomService.getClassroomsByRoomType(roomTypeId));
    }
    
    /**
     * GET classrooms by floor
     */
    @GetMapping("/floor/{floor}")
    public ResponseEntity<List<Classroom>> getClassroomsByFloor(@PathVariable Integer floor) {
        return ResponseEntity.ok(classroomService.getClassroomsByFloor(floor));
    }
    
    /**
     * POST create new classroom
     */
    @PostMapping
    public ResponseEntity<Classroom> createClassroom(@RequestBody Classroom classroom) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classroomService.createClassroom(classroom));
    }
    
    /**
     * PUT update classroom
     */
    @PutMapping("/{id}")
    public ResponseEntity<Classroom> updateClassroom(@PathVariable Long id, @RequestBody Classroom classroom) {
        return ResponseEntity.ok(classroomService.updateClassroom(id, classroom));
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
