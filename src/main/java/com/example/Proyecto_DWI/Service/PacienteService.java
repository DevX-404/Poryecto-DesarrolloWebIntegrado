package com.example.Proyecto_DWI.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Proyecto_DWI.Model.Paciente;
import com.example.Proyecto_DWI.Repository.PacienteRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public List<Paciente> listarTodos() {
        log.info("Listando todos los pacientes");
        return pacienteRepository.findAll();
    }

    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con ID: " + id));
    }

    public Paciente buscarPorDni(String dni) {
        return pacienteRepository.findByDni(dni).orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con DNI: " + dni));
    }

    public List<Paciente> buscarPorNombre(String termino) {
        return pacienteRepository.findByNombreContainingOrApellidoContaining(termino, termino);
    }

    public Paciente registrar(Paciente paciente) {
        if (pacienteRepository.existsByDni(paciente.getDni())){
            throw new IllegalArgumentException("Ya existe un paciente con el DNI: " + paciente.getDni());
        }
        log.info("Registrando nuevo paciente: {} {}", paciente.getNombre(), paciente.getApellido());
        return pacienteRepository.save(paciente);
    }

    public Paciente actualizar(Long id, Paciente datosNuevos) {
        Paciente existente = buscarPorId(id);

        existente.setNombre(datosNuevos.getNombre());
        existente.setApellido(datosNuevos.getApellido());
        existente.setTelefono(datosNuevos.getTelefono());
        existente.setEmail(datosNuevos.getEmail());
        existente.setFechaNacimiento(datosNuevos.getFechaNacimiento());

        log.info("Actualizando paciente con ID: {}", id);
        return pacienteRepository.save(existente);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        pacienteRepository.deleteById(id);
        log.info("Eliminando paciente con ID: {}", id);
    }

}
