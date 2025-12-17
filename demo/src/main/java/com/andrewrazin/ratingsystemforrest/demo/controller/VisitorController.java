package com.andrewrazin.ratingsystemforrest.demo.controller;
import com.andrewrazin.ratingsystemforrest.demo.dto.request.VisitorRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.VisitorResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.service.VisitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Посетители", description = "API для управления посетителями ресторанов")
public class VisitorController {

    private final VisitorService visitorService;

    @Autowired
    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @PostMapping
    @Operation(summary = "Создать нового посетителя")
    public ResponseEntity<VisitorResponseDTO> createVisitor(@Valid @RequestBody VisitorRequestDTO visitorRequestDTO) {
        VisitorResponseDTO createdVisitor = visitorService.save(visitorRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVisitor);
    }

    @GetMapping
    @Operation(summary = "Получить всех посетителей")
    public ResponseEntity<List<VisitorResponseDTO>> getAllVisitors() {
        List<VisitorResponseDTO> visitors = visitorService.findAll();
        return ResponseEntity.ok(visitors);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить посетителя по ID")
    public ResponseEntity<VisitorResponseDTO> getVisitorById(@PathVariable Long id) {
        return visitorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные посетителя")
    public ResponseEntity<VisitorResponseDTO> updateVisitor(
            @PathVariable Long id,
            @Valid @RequestBody VisitorRequestDTO visitorRequestDTO) {
        try {
            VisitorResponseDTO updatedVisitor = visitorService.update(id, visitorRequestDTO);
            return ResponseEntity.ok(updatedVisitor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить посетителя")
    public ResponseEntity<Void> deleteVisitor(@PathVariable Long id) {
        visitorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


