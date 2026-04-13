package com.example.Proyecto_DWI.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Proyecto_DWI.Model.CitaMedica;
import com.example.Proyecto_DWI.Model.Paciente;
import com.example.Proyecto_DWI.Repository.CitaMedicaRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CitaMedicaService {

    private final CitaMedicaRepository citaRepository;
    private final PacienteService pacienteService;

    public CitaMedicaService(CitaMedicaRepository citaRepository, PacienteService pacienteService) {
        this.citaRepository = citaRepository;
        this.pacienteService = pacienteService;
    }

    public List<CitaMedica> listarTodas() {
        return citaRepository.findAll();
    }

    public CitaMedica buscarPorId(Long id){
        return citaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cita no encontrada con ID: " + id));
    }

    public List<CitaMedica> listarPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId);
    }

    public CitaMedica registrar(Long pacienteId, CitaMedica cita) {
        Paciente paciente = pacienteService.buscarPorId(pacienteId);

        if (citaRepository.existeCitaEnHorario(cita.getFechaHora())){
            throw new IllegalArgumentException("Ya existe una cita en el horario: " + cita.getFechaHora());
        }
        cita.setPaciente(paciente);
        log.info("Registrando cita para paciente ID {} en {}", pacienteId, cita.getFechaHora());
        return citaRepository.save(cita);
    }

    public CitaMedica cambiarEstado(Long id, CitaMedica.EstadoCita nuevoEstado) {
        CitaMedica cita =buscarPorId(id);

        if (cita.getEstado() == CitaMedica.EstadoCita.COMPLETADA){
            throw new IllegalStateException("No se puede cambiar el estado de una cita completada");
        }

        cita.setEstado(nuevoEstado);
        return citaRepository.save(cita);
    }

    public void cancelar(Long id) {
        cambiarEstado(id, CitaMedica.EstadoCita.CANCELADA);
    }

}
