package com.example.Proyecto_DWI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.example.Proyecto_DWI.Model.Medico;
import com.example.Proyecto_DWI.Model.Paciente;
import com.example.Proyecto_DWI.Repository.CitaMedicaRepository;
import com.example.Proyecto_DWI.Service.CitaMedicaService;
import com.example.Proyecto_DWI.Service.MedicoService;
import com.example.Proyecto_DWI.Service.PacienteService;

@ExtendWith(MockitoExtension.class)
class CitaMedicaServiceTest {

    @Mock
    private CitaMedicaRepository citaRepository;

    @Mock
    private PacienteService pacienteService;

    @Mock
    private MedicoService medicoService; 

    @InjectMocks
    private CitaMedicaService citaService;

    private Paciente pacienteValido;
    private Medico medicoValido;
    private CitaMedica citaValida;

    @BeforeEach
    void setUp() {
        pacienteValido = Paciente.builder()
                .id(1L).nombre("Juan").apellido("Perez").dni("12345678").build();

        medicoValido = Medico.builder()
                .id(1L).nombre("Carlos").apellido("Gomez").especialidad("Cardiología").cmp("CMP12345").build();

        citaValida = CitaMedica.builder()
                .fechaHora(LocalDateTime.now().plusDays(1)) 
                .motivo("Chequeo preventivo")
                .estado(CitaMedica.EstadoCita.PENDIENTE)
                .build();
    }

    @Test
    @DisplayName("Debe registrar una cita cuando paciente y médico existen y el horario está libre")
    void debeRegistrarCitaCorrectamente() {
        // ARRANGE
        when(pacienteService.buscarPorId(1L)).thenReturn(pacienteValido);
        when(medicoService.buscarPorId(1L)).thenReturn(medicoValido);
        when(citaRepository.existeConflictoMedico(any(), eq(1L))).thenReturn(false); 
        when(citaRepository.save(any())).thenReturn(citaValida);

        // ACT
        CitaMedica resultado = citaService.registrar(1L, 1L, citaValida); 

        // ASSERT
        assertThat(resultado).isNotNull();
        verify(citaRepository, times(1)).save(any(CitaMedica.class));
    }

    @Test
    @DisplayName("No debe registrar cita si el horario del médico ya está ocupado")
    void noDebeRegistrarCitaSiMedicoOcupado() {
        // ARRANGE
        when(citaRepository.existeConflictoMedico(any(), eq(1L))).thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> citaService.registrar(1L, 1L, citaValida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya tiene una cita"); 

        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("No debe registrar cita en una fecha pasada")
    void noDebeRegistrarCitaEnElPasado() {
        // ARRANGE: Cita para ayer
        CitaMedica citaPasada = CitaMedica.builder()
                .fechaHora(LocalDateTime.now().minusDays(1))
                .build();

        // ACT & ASSERT
        assertThatThrownBy(() -> citaService.registrar(1L, 1L, citaPasada))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pasado");

        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("No debe permitir cancelar una cita que ya ha sido completada")
    void noDebeCancelarCitaCompletada() {
        // ARRANGE: Cita ya completada
        CitaMedica citaTerminada = CitaMedica.builder()
                .id(10L)
                .estado(CitaMedica.EstadoCita.COMPLETADA)
                .build();
        
        when(citaRepository.findById(10L)).thenAnswer(invocation -> java.util.Optional.of(citaTerminada));

        // ACT & ASSERT: Intentar cancelar una cita completada debe fallar
        assertThatThrownBy(() -> citaService.cambiarEstado(10L, CitaMedica.EstadoCita.CANCELADA))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("finalizado o ha sido cancelada"); 
    }
}