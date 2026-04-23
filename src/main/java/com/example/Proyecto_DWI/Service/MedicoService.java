package com.example.Proyecto_DWI.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import com.example.Proyecto_DWI.Model.Medico;
import com.example.Proyecto_DWI.Repository.MedicoRepository;

@Service
public class MedicoService {
    private final MedicoRepository medicoRepository;

    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    public List<Medico> listarActivos() {
        return medicoRepository.findByActivoTrue();
    }

    public Medico registrar(Medico medico) {
        if (medicoRepository.existsByCmp(medico.getCmp())) {
            throw new IllegalArgumentException("Ya existe un médico registrado con el CMP: " + medico.getCmp());
        }
        medico.setActivo(true);
        return medicoRepository.save(medico);
    }

    public Medico buscarPorId(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Médico no encontrado"));
    }

    public void eliminarLogico(Long id) {
        Medico medico = buscarPorId(id);
        medico.setActivo(false); 
        medicoRepository.save(medico);
    }
}
