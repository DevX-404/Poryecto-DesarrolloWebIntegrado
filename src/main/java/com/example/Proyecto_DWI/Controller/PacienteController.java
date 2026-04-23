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

import com.example.Proyecto_DWI.Model.Paciente;
import com.example.Proyecto_DWI.Service.PacienteService;

import org.springframework.ui.Model;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String nombre, Model model) {
        if (nombre != null && !nombre.isBlank()) {
            model.addAttribute("pacientes", pacienteService.buscarPorNombre(nombre));
            model.addAttribute("busqueda", nombre);
        } else {
            model.addAttribute("pacientes", pacienteService.listarTodos());
        }
        return "pacientes/lista"; 
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("paciente", new Paciente()); 
        model.addAttribute("titulo", "Nuevo Paciente");
        model.addAttribute("esNuevo", true);
        return "pacientes/formulario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("paciente", pacienteService.buscarPorId(id));
            model.addAttribute("titulo", "Editar Paciente");
            model.addAttribute("esNuevo", false);
        } catch (EntityNotFoundException e) {
            return "redirect:/pacientes";
        }
        return "pacientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("paciente") Paciente paciente, BindingResult result, Model model,
            RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", paciente.getId() == null ? "Nuevo Paciente" : "Editar Paciente");
            return "pacientes/formulario";
        }
        try {
            if (paciente.getId() == null) {
                pacienteService.registrar(paciente);
                flash.addFlashAttribute("mensajeExito", "Paciente registrado correctamente.");
            } else {
                pacienteService.actualizar(paciente.getId(), paciente);
                flash.addFlashAttribute("mensajeExito", "Paciente actualizado correctamente.");
            }
        } catch (Exception e) {
            model.addAttribute("mensajeError", "Error: " + e.getMessage());
            model.addAttribute("titulo", paciente.getId() == null ? "Nuevo Paciente" : "Editar Paciente");
            return "pacientes/formulario";
        }
        return "redirect:/pacientes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            pacienteService.eliminarLogico(id);
            flash.addFlashAttribute("mensajeExito", "Paciente eliminado.");
        } catch (Exception e) {
            flash.addFlashAttribute("mensajeError", "No se pudo eliminar el paciente.");
        }
        return "redirect:/pacientes";
    }

    @GetMapping("/papelera")
    public String verPapelera(Model model) {
        model.addAttribute("pacientes", pacienteService.listarEliminados());
        return "pacientes/papelera";
    }

    @GetMapping("/restaurar/{id}")
    public String restaurar(@PathVariable Long id, RedirectAttributes flash) {
        pacienteService.restaurar(id);
        flash.addFlashAttribute("mensajeExito", "Paciente restaurado.");
        return "redirect:/pacientes";
    }
}
