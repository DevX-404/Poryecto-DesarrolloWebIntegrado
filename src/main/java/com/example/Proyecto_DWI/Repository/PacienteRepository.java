package com.example.Proyecto_DWI.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Proyecto_DWI.Model.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByDni(String dni);

    Optional<Paciente> findByEmail(String email);

    List<Paciente> findByNombreContainingOrApellidoContaining(String nombre, String apellido);

    boolean existsByDni(String dni);

    List<Paciente> findByActivoTrue();
    
    List<Paciente> findByActivoFalse();

    List<Paciente> findByActivoTrueAndNombreContainingOrActivoTrueAndApellidoContaining(String nombre, String apellido);

}
