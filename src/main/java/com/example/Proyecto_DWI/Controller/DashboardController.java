package com.example.Proyecto_DWI.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.Proyecto_DWI.Model.CitaMedica;
import com.example.Proyecto_DWI.Service.CitaMedicaService;
import com.example.Proyecto_DWI.Service.PacienteService;

import org.springframework.ui.Model;

@Controller
public class DashboardController {

    private final PacienteService pacienteService;
    private final CitaMedicaService citaService;

    public DashboardController(PacienteService pacienteService, CitaMedicaService citaService) {
        this.pacienteService = pacienteService;
        this.citaService = citaService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("totalPacientes", pacienteService.listarTodos().size());
        model.addAttribute("totalCitas", citaService.listarTodas().size());
        model.addAttribute("citasPendientes",
            citaService.listarTodas().stream()
                .filter(c -> c.getEstado() == CitaMedica.EstadoCita.PENDIENTE)
                .count());
        model.addAttribute("citasHoy",
            citaService.listarTodas().stream()
                .filter(c -> c.getFechaHora().toLocalDate()
                    .equals(java.time.LocalDate.now()))
                .count());
        model.addAttribute("ultimasCitas",
            citaService.listarTodas().stream()
                .sorted((a, b) -> b.getFechaHora().compareTo(a.getFechaHora()))
                .limit(5)
                .toList());
        return "index";
    }

}
