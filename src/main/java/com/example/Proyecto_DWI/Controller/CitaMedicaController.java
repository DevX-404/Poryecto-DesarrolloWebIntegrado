package com.example.Proyecto_DWI.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.ui.Model;
import com.example.Proyecto_DWI.Model.CitaMedica;
import com.example.Proyecto_DWI.Service.CitaMedicaService;
import com.example.Proyecto_DWI.Service.PacienteService;
import com.example.Proyecto_DWI.Service.MedicoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/citas")
public class CitaMedicaController {

    private final CitaMedicaService citaService;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;

    public CitaMedicaController(CitaMedicaService citaService, PacienteService pacienteService,
            MedicoService medicoService) {
        this.medicoService = medicoService;
        this.citaService = citaService;
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("citas", citaService.listarTodas());
        return "citas/lista";
    }

    @GetMapping("/nueva")
    public String mostrarFormulario(@RequestParam(required = false) Long pacienteId, Model model) {
        model.addAttribute("cita", new CitaMedica());
        model.addAttribute("pacientes", pacienteService.listarTodos());
        model.addAttribute("medicos", medicoService.listarActivos());

        // Enviamos el ID recibido (si existe) para que el HTML lo marque como
        // seleccionado
        model.addAttribute("pacientePreId", pacienteId);
        return "citas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Long pacienteId,
            @RequestParam Long medicoId, // <--- Agregar este parámetro
            @Valid @ModelAttribute("cita") CitaMedica cita,
            BindingResult result,
            Model model,
            RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("pacientes", pacienteService.listarTodos());
            model.addAttribute("medicos", medicoService.listarActivos()); // Para el select
            return "citas/formulario";
        }
        try {
            // Ahora pasamos los 3 argumentos: paciente, medico y la cita
            citaService.registrar(pacienteId, medicoId, cita);
            flash.addFlashAttribute("mensajeExito", "Cita registrada correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/citas";
    }

    @GetMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            citaService.cancelar(id);
            flash.addFlashAttribute("mensajeExito", "Cita cancelada.");
        } catch (Exception e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/citas";
    }

    @GetMapping("/estado/{id}")
    public String cambiarEstado(@PathVariable Long id,
            @RequestParam CitaMedica.EstadoCita nuevoEstado,
            RedirectAttributes flash) {
        try {
            citaService.cambiarEstado(id, nuevoEstado);
            flash.addFlashAttribute("mensajeExito", "Estado actualizado.");
        } catch (Exception e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/citas";
    }

}
