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

import com.maplewood.common.dto.RoomTypeDTO;
import com.maplewood.common.mapper.RoomTypeMapper;
import com.maplewood.school.entity.RoomType;
import com.maplewood.school.service.RoomTypeService;

import jakarta.validation.Valid;

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
    public ResponseEntity<List<RoomTypeDTO>> getAllRoomTypes() {
        return ResponseEntity.ok(roomTypeService.getAllRoomTypes()
            .stream()
            .map(RoomTypeMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * GET room type by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomTypeDTO> getRoomTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(RoomTypeMapper.toDTO(roomTypeService.getRoomTypeById(id)));
    }
    
    /**
     * GET room type by name
     */
    @GetMapping("/search/name")
    public ResponseEntity<RoomTypeDTO> getRoomTypeByName(@RequestParam String name) {
        return ResponseEntity.ok(RoomTypeMapper.toDTO(roomTypeService.getRoomTypeByName(name)));
    }
    
    /**
     * POST create new room type
     */
    @PostMapping
    public ResponseEntity<RoomTypeDTO> createRoomType(@Valid @RequestBody RoomTypeDTO roomTypeDTO) {
        RoomType entity = RoomTypeMapper.toEntity(roomTypeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(RoomTypeMapper.toDTO(roomTypeService.createRoomType(entity)));
    }
    
    /**
     * PUT update room type
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoomTypeDTO> updateRoomType(@PathVariable Long id, @Valid @RequestBody RoomTypeDTO roomTypeDTO) {
        RoomType entity = RoomTypeMapper.toEntity(roomTypeDTO);
        return ResponseEntity.ok(RoomTypeMapper.toDTO(roomTypeService.updateRoomType(id, entity)));
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
