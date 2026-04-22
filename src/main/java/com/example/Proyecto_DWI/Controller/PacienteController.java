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

    // GET /pacientes → muestra la lista
    // Model es como una "mochila" donde metes datos para que la plantilla los use
    @GetMapping
    public String listar(@RequestParam(required = false) String nombre, Model model) {
        if (nombre != null && !nombre.isBlank()) {
            model.addAttribute("pacientes", pacienteService.buscarPorNombre(nombre));
            model.addAttribute("busqueda", nombre);
        } else {
            model.addAttribute("pacientes", pacienteService.listarTodos());
        }
        return "pacientes/lista"; // → busca templates/pacientes/lista.html
    }

    // GET /pacientes/nuevo → muestra el formulario vacío
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("paciente", new Paciente()); // objeto vacío para el form
        model.addAttribute("titulo", "Nuevo Paciente");
        model.addAttribute("esNuevo", true);
        return "pacientes/formulario";
    }

    // GET /pacientes/editar/1 → muestra el formulario con datos del paciente
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

    // POST /pacientes/guardar → procesa el formulario
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("paciente") Paciente paciente,
                          BindingResult result, // captura errores de validación
                          Model model,
                          RedirectAttributes flash) { // mensajes que sobreviven el redirect
        if (result.hasErrors()) {
            model.addAttribute("titulo", paciente.getId() == null ? "Nuevo Paciente" : "Editar Paciente");
            model.addAttribute("esNuevo", paciente.getId() == null);
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
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("esNuevo", paciente.getId() == null);
            model.addAttribute("titulo", "Error al guardar");
            return "pacientes/formulario";
        }
        return "redirect:/pacientes";
    }

    // GET /pacientes/eliminar/1
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            pacienteService.eliminar(id);
            flash.addFlashAttribute("mensajeExito", "Paciente eliminado.");
        } catch (Exception e) {
            flash.addFlashAttribute("mensajeError", "No se pudo eliminar el paciente.");
        }
        return "redirect:/pacientes";
    }

}
