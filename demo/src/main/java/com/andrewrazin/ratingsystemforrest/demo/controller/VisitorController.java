package com.andrewrazin.ratingsystemforrest.demo.controller;

import com.andrewrazin.ratingsystemforrest.demo.entity.Visitor;
import com.andrewrazin.ratingsystemforrest.demo.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitors")
public class VisitorController {

    private final VisitorService visitorService;

    @Autowired
    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @PostMapping
    public ResponseEntity<Visitor> createVisitor(@RequestBody Visitor visitor) {
        Visitor savedVisitor = visitorService.save(visitor);
        return ResponseEntity.ok(savedVisitor);
    }

    @GetMapping
    public ResponseEntity<List<Visitor>> getAllVisitors() {
        List<Visitor> visitors = visitorService.findAll();
        return ResponseEntity.ok(visitors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Visitor> getVisitorById(@PathVariable Long id) {
        return visitorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisitor(@PathVariable Long id) {
        visitorService.remove(id);
        return ResponseEntity.ok().build();
    }
}
