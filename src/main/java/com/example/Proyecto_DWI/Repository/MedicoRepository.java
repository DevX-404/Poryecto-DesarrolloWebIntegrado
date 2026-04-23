package com.example.Proyecto_DWI.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Proyecto_DWI.Model.Medico;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    List<Medico> findByActivoTrue();
    List<Medico> findByActivoFalse();
    
    Optional<Medico> findByCmp(String cmp);
    
    boolean existsByCmp(String cmp);
}