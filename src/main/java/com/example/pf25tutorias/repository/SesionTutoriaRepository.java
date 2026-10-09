package com.example.pf25tutorias.repository;

import com.example.pf25tutorias.model.SesionTutoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SesionTutoriaRepository extends JpaRepository<SesionTutoria, Long> {
}