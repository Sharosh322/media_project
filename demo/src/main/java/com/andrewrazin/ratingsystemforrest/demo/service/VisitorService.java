package com.andrewrazin.ratingsystemforrest.demo.service;
import com.andrewrazin.ratingsystemforrest.demo.dto.request.VisitorRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.VisitorResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.entity.Visitor;
import com.andrewrazin.ratingsystemforrest.demo.mapper.VisitorMapper;
import com.andrewrazin.ratingsystemforrest.demo.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final VisitorMapper visitorMapper;

    @Autowired
    public VisitorService(VisitorRepository visitorRepository, VisitorMapper visitorMapper) {
        this.visitorRepository = visitorRepository;
        this.visitorMapper = visitorMapper;
    }

    public VisitorResponseDTO save(VisitorRequestDTO visitorRequestDTO) {
        Visitor visitor = visitorMapper.toEntity(visitorRequestDTO);
        Visitor savedVisitor = visitorRepository.save(visitor);
        return visitorMapper.toResponseDTO(savedVisitor);
    }

    public List<VisitorResponseDTO> findAll() {
        List<Visitor> visitors = visitorRepository.findAll();
        return visitorMapper.toResponseDTOList(visitors);
    }

    public Optional<VisitorResponseDTO> findById(Long id) {
        return visitorRepository.findById(id)
                .map(visitorMapper::toResponseDTO);
    }

    public VisitorResponseDTO update(Long id, VisitorRequestDTO visitorRequestDTO) {
        Visitor existingVisitor = visitorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visitor not found with id: " + id));

        visitorMapper.updateEntityFromDTO(visitorRequestDTO, existingVisitor);
        Visitor updatedVisitor = visitorRepository.save(existingVisitor);
        return visitorMapper.toResponseDTO(updatedVisitor);
    }

    public void delete(Long id) {
        visitorRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return visitorRepository.existsById(id);
    }
}
