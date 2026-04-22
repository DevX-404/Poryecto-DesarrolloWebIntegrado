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

        // 2. Verificación de Email (Esto evitará el error 500 que tuviste)
        if (paciente.getEmail() != null && !paciente.getEmail().isBlank()) {
            if (pacienteRepository.findByEmail(paciente.getEmail()).isPresent()) {
                throw new IllegalArgumentException("El correo electrónico ya está registrado por otro paciente.");
            }
        }

        paciente.setActivo(true); // Aseguramos que inicie como activo
        log.info("Registrando nuevo paciente: {} {}", paciente.getNombre(), paciente.getApellido());
        return pacienteRepository.save(paciente);
    }

    public Paciente actualizar(Long id, Paciente datosNuevos) {
        Paciente existente = buscarPorId(id);

        // Validar que el nuevo DNI no lo tenga otro paciente
        pacienteRepository.findByDni(datosNuevos.getDni())
                .ifPresent(p -> {
                    if (!p.getId().equals(id))
                        throw new IllegalArgumentException("El DNI ya pertenece a otro registro.");
                });

        // Validar que el nuevo Email no lo tenga otro paciente
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
        p.setActivo(false); // No borramos, desactivamos (Integridad referencial)
        pacienteRepository.save(p);
        log.info("Paciente con ID {} desactivado", id);
    }

    // Métodos para la Restauración
    public List<Paciente> listarEliminados() {
        return pacienteRepository.findByActivoFalse();
    }

    public void restaurar(Long id) {
        Paciente p = buscarPorId(id);
        p.setActivo(true);
        pacienteRepository.save(p);
    }

}
