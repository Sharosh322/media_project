package com.andrewrazin.ratingsystemforrest.demo.mapper;

import com.andrewrazin.ratingsystemforrest.demo.dto.request.VisitorRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.VisitorResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.entity.Visitor;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VisitorMapper {

    @Mapping(target = "id", ignore = true)
    Visitor toEntity(VisitorRequestDTO visitorRequestDTO);

    VisitorResponseDTO toResponseDTO(Visitor visitor);

    List<VisitorResponseDTO> toResponseDTOList(List<Visitor> visitors);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(VisitorRequestDTO visitorRequestDTO, @MappingTarget Visitor visitor);
}