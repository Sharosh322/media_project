package com.andrewrazin.ratingsystemforrest.demo.controller;

import com.andrewrazin.ratingsystemforrest.demo.dto.request.VisitorRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.VisitorResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.service.VisitorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisitorController.class)
class VisitorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VisitorService visitorService;

    @Test
    void createVisitor_ShouldReturnCreatedResponse() throws Exception {
        // Arrange
        VisitorRequestDTO requestDTO = new VisitorRequestDTO("John Doe", 25, "Male");
        VisitorResponseDTO responseDTO = new VisitorResponseDTO(1L, "John Doe", 25, "Male");

        when(visitorService.save(any(VisitorRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.gender").value("Male"));

        verify(visitorService).save(any(VisitorRequestDTO.class));
    }

    @Test
    void createVisitor_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        VisitorRequestDTO invalidRequest = new VisitorRequestDTO(null, -5, null);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(visitorService, never()).save(any(VisitorRequestDTO.class));
    }

    @Test
    void getAllVisitors_ShouldReturnListOfVisitors() throws Exception {
        // Arrange
        VisitorResponseDTO visitor1 = new VisitorResponseDTO(1L, "John Doe", 25, "Male");
        VisitorResponseDTO visitor2 = new VisitorResponseDTO(2L, "Jane Doe", 30, "Female");

        when(visitorService.findAll()).thenReturn(Arrays.asList(visitor1, visitor2));

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Jane Doe"));

        verify(visitorService).findAll();
    }

    @Test
    void getVisitorById_WhenVisitorExists_ShouldReturnVisitor() throws Exception {
        // Arrange
        VisitorResponseDTO visitor = new VisitorResponseDTO(1L, "John Doe", 25, "Male");
        when(visitorService.findById(1L)).thenReturn(Optional.of(visitor));

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.age").value(25));

        verify(visitorService).findById(1L);
    }

    @Test
    void getVisitorById_WhenVisitorNotExists_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(visitorService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(visitorService).findById(999L);
    }

    @Test
    void updateVisitor_WhenVisitorExists_ShouldReturnUpdatedVisitor() throws Exception {
        // Arrange
        VisitorRequestDTO updateDTO = new VisitorRequestDTO("John Updated", 26, "Male");
        VisitorResponseDTO updatedVisitor = new VisitorResponseDTO(1L, "John Updated", 26, "Male");

        when(visitorService.update(eq(1L), any(VisitorRequestDTO.class))).thenReturn(updatedVisitor);

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.age").value(26));

        verify(visitorService).update(eq(1L), any(VisitorRequestDTO.class));
    }

    @Test
    void updateVisitor_WhenVisitorNotExists_ShouldReturnNotFound() throws Exception {
        // Arrange
        VisitorRequestDTO updateDTO = new VisitorRequestDTO("John Updated", 26, "Male");
        when(visitorService.update(eq(999L), any(VisitorRequestDTO.class)))
                .thenThrow(new RuntimeException("Visitor not found"));

        // Act & Assert
        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        verify(visitorService).update(eq(999L), any(VisitorRequestDTO.class));
    }

    @Test
    void deleteVisitor_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(visitorService).delete(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(visitorService).delete(1L);
    }
}
