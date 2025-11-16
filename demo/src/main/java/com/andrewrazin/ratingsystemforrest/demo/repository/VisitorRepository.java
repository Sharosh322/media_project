package com.andrewrazin.ratingsystemforrest.demo.repository;

import com.andrewrazin.ratingsystemforrest.demo.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
}
