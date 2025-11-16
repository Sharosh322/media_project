package com.andrewrazin.ratingsystemforrest.demo.service;

import com.andrewrazin.ratingsystemforrest.demo.entity.Visitor;
import com.andrewrazin.ratingsystemforrest.demo.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VisitorService {

    private final VisitorRepository visitorRepository;

    @Autowired
    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    public Visitor save(Visitor visitor) {
        return visitorRepository.save(visitor);
    }

    public void remove(Long id) {
        visitorRepository.deleteById(id);
    }

    public List<Visitor> findAll() {
        return visitorRepository.findAll();
    }

    public Optional<Visitor> findById(Long id) {
        return visitorRepository.findById(id);
    }

    public boolean existsById(Long id) {
        return visitorRepository.existsById(id);
    }
}
