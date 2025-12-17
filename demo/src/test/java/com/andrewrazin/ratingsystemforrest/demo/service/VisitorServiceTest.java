package com.andrewrazin.ratingsystemforrest.demo.service;

import com.andrewrazin.ratingsystemforrest.demo.dto.request.VisitorRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.VisitorResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.entity.Visitor;
import com.andrewrazin.ratingsystemforrest.demo.mapper.VisitorMapper;
import com.andrewrazin.ratingsystemforrest.demo.repository.VisitorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitorServiceTest {

    @Mock
    private VisitorRepository visitorRepository;

    @Mock
    private VisitorMapper visitorMapper;

    @InjectMocks
    private VisitorService visitorService;

    private Visitor visitor;
    private VisitorRequestDTO visitorRequestDTO;
    private VisitorResponseDTO visitorResponseDTO;

    @BeforeEach
    void setUp() {
        // Тестовые данные
        visitorRequestDTO = new VisitorRequestDTO("John Doe", 25, "Male");
        visitorResponseDTO = new VisitorResponseDTO(1L, "John Doe", 25, "Male");

        visitor = new Visitor();
        visitor.setId(1L);
        visitor.setName("John Doe");
        visitor.setAge(25);
        visitor.setGender("Male");
    }

    @Test
    void save_ShouldReturnVisitorResponseDTO() {
        // Arrange
        when(visitorMapper.toEntity(visitorRequestDTO)).thenReturn(visitor);
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toResponseDTO(visitor)).thenReturn(visitorResponseDTO);

        // Act
        VisitorResponseDTO result = visitorService.save(visitorRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("John Doe", result.name());
        assertEquals(25, result.age());
        assertEquals("Male", result.gender());

        verify(visitorMapper).toEntity(visitorRequestDTO);
        verify(visitorRepository).save(visitor);
        verify(visitorMapper).toResponseDTO(visitor);
    }

    @Test
    void findAll_ShouldReturnListOfVisitorResponseDTOs() {
        // Arrange
        List<Visitor> visitors = Arrays.asList(visitor);
        List<VisitorResponseDTO> expectedResponse = Arrays.asList(visitorResponseDTO);

        when(visitorRepository.findAll()).thenReturn(visitors);
        when(visitorMapper.toResponseDTOList(visitors)).thenReturn(expectedResponse);

        // Act
        List<VisitorResponseDTO> result = visitorService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedResponse, result);

        verify(visitorRepository).findAll();
        verify(visitorMapper).toResponseDTOList(visitors);
    }

    @Test
    void findById_WhenVisitorExists_ShouldReturnVisitorResponseDTO() {
        // Arrange
        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(visitorMapper.toResponseDTO(visitor)).thenReturn(visitorResponseDTO);

        // Act
        Optional<VisitorResponseDTO> result = visitorService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(visitorResponseDTO, result.get());

        verify(visitorRepository).findById(1L);
        verify(visitorMapper).toResponseDTO(visitor);
    }

    @Test
    void findById_WhenVisitorNotExists_ShouldReturnEmptyOptional() {
        // Arrange
        when(visitorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<VisitorResponseDTO> result = visitorService.findById(999L);

        // Assert
        assertFalse(result.isPresent());

        verify(visitorRepository).findById(999L);
        verify(visitorMapper, never()).toResponseDTO(any());
    }

    @Test
    void update_WhenVisitorExists_ShouldReturnUpdatedVisitor() {
        // Arrange
        VisitorRequestDTO updateDTO = new VisitorRequestDTO("Jane Doe", 30, "Female");
        Visitor updatedVisitor = new Visitor("Jane Doe", 30, "Female");
        updatedVisitor.setId(1L);
        VisitorResponseDTO updatedResponse = new VisitorResponseDTO(1L, "Jane Doe", 30, "Female");

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(visitorRepository.save(any(Visitor.class))).thenReturn(updatedVisitor);
        when(visitorMapper.toResponseDTO(any(Visitor.class))).thenReturn(updatedResponse);

        // Act
        VisitorResponseDTO result = visitorService.update(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Jane Doe", result.name());
        assertEquals(30, result.age());
        assertEquals("Female", result.gender());

        verify(visitorRepository).findById(1L);
        verify(visitorMapper).updateEntityFromDTO(updateDTO, visitor);
        verify(visitorRepository).save(visitor);
        verify(visitorMapper).toResponseDTO(any(Visitor.class));
    }

    @Test
    void update_WhenVisitorNotExists_ShouldThrowException() {
        // Arrange
        VisitorRequestDTO updateDTO = new VisitorRequestDTO("Jane Doe", 30, "Female");
        when(visitorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> visitorService.update(999L, updateDTO));

        assertEquals("Visitor not found with id: 999", exception.getMessage());

        verify(visitorRepository).findById(999L);
        verify(visitorRepository, never()).save(any());
    }

    @Test
    void delete_WhenVisitorExists_ShouldDeleteVisitor() {
        // Arrange
        when(visitorRepository.existsById(1L)).thenReturn(true);

        // Act
        visitorService.delete(1L);

        // Assert
        verify(visitorRepository).existsById(1L);
        verify(visitorRepository).deleteById(1L);
    }

    @Test
    void delete_WhenVisitorNotExists_ShouldThrowException() {
        // Arrange
        when(visitorRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> visitorService.delete(999L));

        assertEquals("Visitor not found with id: 999", exception.getMessage());

        verify(visitorRepository).existsById(999L);
        verify(visitorRepository, never()).deleteById(any());
    }
}
