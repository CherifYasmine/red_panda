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

import com.maplewood.school.entity.RoomType;
import com.maplewood.school.service.RoomTypeService;

/**
 * REST controller for RoomType endpoints
 * Provides CRUD operations for room types
 */
@RestController
@RequestMapping("/api/v1/room-types")
@CrossOrigin(origins = "*")
public class RoomTypeController {
    
    @Autowired
    private RoomTypeService roomTypeService;
    
    /**
     * GET all room types
     */
    @GetMapping
    public ResponseEntity<List<RoomType>> getAllRoomTypes() {
        return ResponseEntity.ok(roomTypeService.getAllRoomTypes());
    }
    
    /**
     * GET room type by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomType> getRoomTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(roomTypeService.getRoomTypeById(id));
    }
    
    /**
     * GET room type by name
     */
    @GetMapping("/search/name")
    public ResponseEntity<RoomType> getRoomTypeByName(@RequestParam String name) {
        return ResponseEntity.ok(roomTypeService.getRoomTypeByName(name));
    }
    
    /**
     * POST create new room type
     */
    @PostMapping
    public ResponseEntity<RoomType> createRoomType(@RequestBody RoomType roomType) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomTypeService.createRoomType(roomType));
    }
    
    /**
     * PUT update room type
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoomType> updateRoomType(@PathVariable Long id, @RequestBody RoomType roomType) {
        return ResponseEntity.ok(roomTypeService.updateRoomType(id, roomType));
    }
    
    /**
     * DELETE room type
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomType(@PathVariable Long id) {
        roomTypeService.deleteRoomType(id);
        return ResponseEntity.noContent().build();
    }
}
