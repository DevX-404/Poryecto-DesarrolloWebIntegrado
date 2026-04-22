package com.example.Proyecto_DWI.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.Proyecto_DWI.Service.CitaMedicaService;
import com.example.Proyecto_DWI.Service.PacienteService;
import com.example.Proyecto_DWI.Service.MedicoService; // <--- Nuevo

import org.springframework.ui.Model;

@Controller
public class DashboardController {

    private final PacienteService pacienteService;
    private final CitaMedicaService citaService;
    private final MedicoService medicoService; // <--- Nuevo



    public DashboardController(PacienteService pacienteService, CitaMedicaService citaService, MedicoService medicoService) { // <--- Nuevo
        this.medicoService = medicoService;
        this.pacienteService = pacienteService;
        this.citaService = citaService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        // Estadísticas generales
        model.addAttribute("totalPacientes", pacienteService.listarTodos().size());
        model.addAttribute("totalMedicos", medicoService.listarActivos().size()); // <--- Nuevo
        model.addAttribute("totalCitas", citaService.listarTodas().size());

        // Filtros de citas para hoy
        long citasHoy = citaService.listarTodas().stream()
                .filter(c -> c.getFechaHora().toLocalDate().equals(java.time.LocalDate.now()))
                .count();
        model.addAttribute("citasHoy", citasHoy);

        // Lista de las últimas 5 citas
        model.addAttribute("ultimasCitas",
                citaService.listarTodas().stream()
                        .sorted((a, b) -> b.getFechaHora().compareTo(a.getFechaHora()))
                        .limit(5)
                        .toList());

        return "index";
    }

}
