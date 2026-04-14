package com.example.Proyecto_DWI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.Proyecto_DWI.Model.CitaMedica;
import com.example.Proyecto_DWI.Model.Paciente;
import com.example.Proyecto_DWI.Repository.CitaMedicaRepository;
import com.example.Proyecto_DWI.Service.CitaMedicaService;
import com.example.Proyecto_DWI.Service.PacienteService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CitaMedicaServiceTest {

    @Mock
    private CitaMedicaRepository citaRepository;

    @Mock
    private PacienteService pacienteService;

    @InjectMocks
    private CitaMedicaService citaService;

    private Paciente pacienteValido;
    private CitaMedica citaValida;


    @BeforeEach
    void setUp(){
        pacienteValido = Paciente.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .dni("12345678")
                .build();

        citaValida = CitaMedica.builder()
                .fechaHora(LocalDateTime.now().plusDays(1))
                .motivo("General")
                .build();
    }

    @Test
    @DisplayName("Debe registrar una cita cuando el paciente existe y el horario está libre")
    void debeRegistrarCitaCorrectamente() {
        // ARRANGE: configuramos qué devolverán los mocks
        when(pacienteService.buscarPorId(1L)).thenReturn(pacienteValido);
        when(citaRepository.existeCitaEnHorario(any())).thenReturn(false);
        when(citaRepository.save(any())).thenReturn(citaValida);

        // ACT: ejecutamos el método que queremos probar
        CitaMedica resultado = citaService.registrar(1L, citaValida);

        // ASSERT: verificamos que el resultado es el esperado
        assertThat(resultado).isNotNull();
        verify(citaRepository, times(1)).save(any(CitaMedica.class));
    }

    @Test
    @DisplayName("No debe registrar cita si el paciente no existe")
    void noDebeRegistrarCitaSiPacienteNoExiste() {
        // Mockito simula que buscar el paciente lanza una excepción
        when(pacienteService.buscarPorId(99L))
                .thenThrow(new EntityNotFoundException("Paciente no encontrado con ID: 99"));

        // assertThatThrownBy verifica que el método lanza la excepción correcta
        assertThatThrownBy(() -> citaService.registrar(99L, citaValida))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Paciente no encontrado");

        // El repositorio nunca debe guardar si el paciente no existe
        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("No debe registrar cita si el horario ya está ocupado")
    void noDebeRegistrarCitaSiHorarioOcupado() {
        when(pacienteService.buscarPorId(1L)).thenReturn(pacienteValido);
        // Simulamos que el horario YA está ocupado
        when(citaRepository.existeCitaEnHorario(any())).thenReturn(true);

        assertThatThrownBy(() -> citaService.registrar(1L, citaValida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe una cita")
                .hasMessageContaining("horario");

        verify(citaRepository, never()).save(any());
    }

}
