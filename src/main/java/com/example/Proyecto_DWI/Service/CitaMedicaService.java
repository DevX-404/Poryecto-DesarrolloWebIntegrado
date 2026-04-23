package com.example.Proyecto_DWI.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Proyecto_DWI.Model.CitaMedica;
import com.example.Proyecto_DWI.Repository.CitaMedicaRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CitaMedicaService {

    private final CitaMedicaRepository citaRepository;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;

    public CitaMedicaService(CitaMedicaRepository citaRepository, 
                             PacienteService pacienteService,
                             MedicoService medicoService) {
        this.citaRepository = citaRepository;
        this.pacienteService = pacienteService;
        this.medicoService = medicoService;
    }

    public List<CitaMedica> listarTodas() {
        return citaRepository.findAll();
    }

    public CitaMedica buscarPorId(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada con ID: " + id));
    }

    public List<CitaMedica> listarPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId);
    }

    public CitaMedica registrar(Long pacienteId, Long medicoId, CitaMedica cita) {
        // Validar fecha futura
        if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede agendar citas en el pasado.");
        }

        if (citaRepository.existeConflictoMedico(cita.getFechaHora(), medicoId)) {
            throw new IllegalArgumentException("El médico ya tiene una cita en ese horario.");
        }

        cita.setPaciente(pacienteService.buscarPorId(pacienteId));
        cita.setMedico(medicoService.buscarPorId(medicoId)); 
        return citaRepository.save(cita);
    }

    public CitaMedica cambiarEstado(Long id, CitaMedica.EstadoCita nuevoEstado) {
        CitaMedica cita = buscarPorId(id);

        if (cita.getEstado() == CitaMedica.EstadoCita.COMPLETADA ||
                cita.getEstado() == CitaMedica.EstadoCita.CANCELADA) {
            throw new IllegalStateException("No se puede modificar una cita que ya ha finalizado o ha sido cancelada.");
        }

        cita.setEstado(nuevoEstado);
        return citaRepository.save(cita);
    }

    public void cancelar(Long id) {
        cambiarEstado(id, CitaMedica.EstadoCita.CANCELADA);
    }

}
