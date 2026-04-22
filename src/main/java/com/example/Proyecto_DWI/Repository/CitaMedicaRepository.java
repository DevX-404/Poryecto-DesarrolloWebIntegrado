package com.example.Proyecto_DWI.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.Proyecto_DWI.Model.CitaMedica;

@Repository
public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Long> {

    List<CitaMedica> findByPacienteId(Long pacienteId);

    List<CitaMedica> findByEstado(CitaMedica.EstadoCita estado);

    @Query("SELECT COUNT(c) > 0 FROM CitaMedica c WHERE c.fechaHora = :fecha " +
           "AND c.medico.id = :medicoId AND c.estado != 'CANCELADA'")
    boolean existeConflictoMedico(LocalDateTime fecha, Long medicoId);
}
