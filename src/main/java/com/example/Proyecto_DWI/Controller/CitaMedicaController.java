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
    public String guardar(@RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) Long medicoId,
            @ModelAttribute("cita") CitaMedica cita, // <--- SIN @Valid para evitar el conflicto
            BindingResult result,
            Model model,
            RedirectAttributes flash) {

        // 1. Validaciones manuales básicas
        if (pacienteId == null)
            result.rejectValue("paciente", "error", "El paciente es obligatorio");
        if (medicoId == null)
            result.rejectValue("medico", "error", "El médico es obligatorio");
        if (cita.getMotivo() == null || cita.getMotivo().isBlank())
            result.rejectValue("motivo", "error", "El motivo es obligatorio");

        // 2. Validación de fecha manual (Para que aparezca junto con los otros errores)
        if (cita.getFechaHora() == null) {
            result.rejectValue("fechaHora", "error", "La fecha y hora son obligatorias");
        } else if (cita.getFechaHora().isBefore(java.time.LocalDateTime.now())) {
            result.rejectValue("fechaHora", "error", "No se puede agendar citas en el pasado."); // <--- ALERTA
                                                                                                 // ESPECÍFICA
        }

        // 3. Ahora sí, si hay CUALQUIER error (incluyendo fecha pasada), regresamos
        if (result.hasErrors()) {
            prepararModeloFormulario(model, pacienteId, medicoId);
            return "citas/formulario";
        }

        try {
            citaService.registrar(pacienteId, medicoId, cita);
            flash.addFlashAttribute("mensajeExito", "Cita registrada correctamente.");
            return "redirect:/citas";
        } catch (IllegalArgumentException e) {
            // Otros errores de lógica (ej: médico ocupado)
            result.rejectValue("medico", "error", e.getMessage());
            prepararModeloFormulario(model, pacienteId, medicoId);
            return "citas/formulario";
        }
    }

    private void prepararModeloFormulario(Model model, Long pId, Long mId) {
        model.addAttribute("pacientes", pacienteService.listarTodos());
        model.addAttribute("medicos", medicoService.listarActivos());
        model.addAttribute("pacientePreId", pId); // Retiene el paciente seleccionado
        model.addAttribute("medicoPreId", mId); // Retiene el médico seleccionado
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
