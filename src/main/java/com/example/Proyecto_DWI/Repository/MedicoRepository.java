package com.example.Proyecto_DWI.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Proyecto_DWI.Model.Medico;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    // Para listar solo doctores disponibles en el consultorio [cite: 182]
    List<Medico> findByActivoTrue();
    
    // El CMP es el "DNI" del médico, debe ser único [cite: 112]
    Optional<Medico> findByCmp(String cmp);
    
    boolean existsByCmp(String cmp);
}