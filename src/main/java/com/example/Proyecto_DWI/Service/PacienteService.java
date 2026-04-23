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
        return pacienteRepository.findByActivoTrue();
    }

    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con ID: " + id));
    }

    public Paciente buscarPorDni(String dni) {
        return pacienteRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con DNI: " + dni));
    }

    public List<Paciente> buscarPorNombre(String termino) {
        return pacienteRepository.findByActivoTrueAndNombreContainingOrActivoTrueAndApellidoContaining(termino,
                termino);
    }

    public Paciente registrar(Paciente paciente) {
        // 1. Verificación de DNI
        if (pacienteRepository.existsByDni(paciente.getDni())) {
            throw new IllegalArgumentException("Ya existe un paciente con el DNI: " + paciente.getDni());
        }

        // 2. Verificación de Email 
        if (paciente.getEmail() != null && !paciente.getEmail().isBlank()) {
            if (pacienteRepository.findByEmail(paciente.getEmail()).isPresent()) {
                throw new IllegalArgumentException("El correo electrónico ya está registrado por otro paciente.");
            }
        }

        if (paciente.getEmail() == null || paciente.getEmail().isBlank()) {
            throw new IllegalArgumentException("El registro requiere obligatoriamente un correo electrónico.");
        }

        if ((paciente.getTelefono() == null || paciente.getTelefono().isBlank()) &&
                (paciente.getEmail() == null || paciente.getEmail().isBlank())) {
            throw new IllegalArgumentException("Debe proporcionar al menos un medio de contacto (Teléfono o Email).");
        }

        paciente.setActivo(true); 
        log.info("Registrando nuevo paciente: {} {}", paciente.getNombre(), paciente.getApellido());
        return pacienteRepository.save(paciente);
    }

    public Paciente actualizar(Long id, Paciente datosNuevos) {
        Paciente existente = buscarPorId(id);

        pacienteRepository.findByDni(datosNuevos.getDni())
                .ifPresent(p -> {
                    if (!p.getId().equals(id))
                        throw new IllegalArgumentException("El DNI ya pertenece a otro registro.");
                });

        if (datosNuevos.getEmail() != null && !datosNuevos.getEmail().isBlank()) {
            pacienteRepository.findByEmail(datosNuevos.getEmail())
                    .ifPresent(p -> {
                        if (!p.getId().equals(id))
                            throw new IllegalArgumentException("El Email ya pertenece a otro registro.");
                    });
        }

        log.info("Recibida fecha de nacimiento: {}", datosNuevos.getFechaNacimiento());

        existente.setNombre(datosNuevos.getNombre());
        existente.setApellido(datosNuevos.getApellido());
        existente.setTelefono(datosNuevos.getTelefono());
        existente.setEmail(datosNuevos.getEmail());
        existente.setFechaNacimiento(datosNuevos.getFechaNacimiento());

        return pacienteRepository.save(existente);
    }

    public void eliminarLogico(Long id) {
        Paciente p = buscarPorId(id);
        p.setActivo(false); 
        pacienteRepository.save(p);
        log.info("Paciente con ID {} desactivado", id);
    }

    public List<Paciente> listarEliminados() {
        return pacienteRepository.findByActivoFalse();
    }

    public void restaurar(Long id) {
        Paciente p = buscarPorId(id);
        p.setActivo(true);
        pacienteRepository.save(p);
    }

}
