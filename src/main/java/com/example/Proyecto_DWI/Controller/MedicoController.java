package com.example.Proyecto_DWI.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Proyecto_DWI.Model.Medico;
import com.example.Proyecto_DWI.Service.MedicoService;
import com.example.Proyecto_DWI.Repository.MedicoRepository; // Necesario para listar inactivos

@Controller
@RequestMapping("/medicos")
public class MedicoController {
    private final MedicoService medicoService;
    private final MedicoRepository medicoRepository;

    public MedicoController(MedicoService medicoService, MedicoRepository medicoRepository) {
        this.medicoService = medicoService;
        this.medicoRepository = medicoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("medicos", medicoService.listarActivos());
        return "medicos/lista"; // Debes crear esta vista similar a pacientes/lista
    }

    @GetMapping("/nuevo")
    public String formulario(Model model) {
        model.addAttribute("medico", new Medico());
        model.addAttribute("titulo", "Registrar Médico");
        return "medicos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Medico medico, RedirectAttributes flash) {
        try {
            medicoService.registrar(medico);
            flash.addFlashAttribute("mensajeExito", "Médico registrado con éxito.");
            return "redirect:/medicos";
        } catch (Exception e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/medicos/nuevo";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("medico", medicoService.buscarPorId(id));
        model.addAttribute("titulo", "Editar Médico");
        return "medicos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String desactivar(@PathVariable Long id, RedirectAttributes flash) {
        medicoService.eliminarLogico(id);
        flash.addFlashAttribute("mensajeExito", "Médico dado de baja (Inactivo).");
        return "redirect:/medicos";
    }

    @GetMapping("/papelera")
    public String verInactivos(Model model) {
        model.addAttribute("medicos", medicoRepository.findByActivoFalse()); // Necesitas crear este método en el repo
        return "medicos/papelera";
    }
}