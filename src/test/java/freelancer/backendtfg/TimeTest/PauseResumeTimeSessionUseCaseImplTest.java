package freelancer.backendtfg.TimeTest;

import freelancer.backendtfg.application.useCaseImpl.timeUseCaseImpl.PauseResumeTimeSessionUseCaseImpl;
import freelancer.backendtfg.domain.mapper.TimeMapper;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para PauseResumeTimeSessionUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear pausa y reanudación de sesiones de tiempo
 * - Verificar que solo sesiones activas pueden pausarse/reanudarse
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Pausar/Reanudar Sesión de Tiempo")
class PauseResumeTimeSessionUseCaseImplTest {

    @Mock
    private TimeRepositoryPort timeRepository;

    @Mock
    private TimeMapper timeMapper;

    @InjectMocks
    private PauseResumeTimeSessionUseCaseImpl pauseResumeTimeSessionUseCase;

    private String userEmail;
    private TimeEntity activeSession;
    private TimeSessionOutputDto expectedOutputDto;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";

        activeSession = new TimeEntity();
        activeSession.setId(1L);
        activeSession.setActive(true);
        activeSession.setPaused(false);

        expectedOutputDto = new TimeSessionOutputDto();
    }

    @Test
    @DisplayName("Debería pausar sesión activa correctamente")
    void deberiaPausarSesionActiva() {
        // ARRANGE
        when(timeRepository.findActiveSessionByUserEmail(anyString())).thenReturn(Optional.of(activeSession));
        when(timeRepository.pauseSession(anyLong())).thenReturn(Optional.of(activeSession));
        when(timeMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        TimeSessionOutputDto result = pauseResumeTimeSessionUseCase.pauseSession(userEmail);

        // ASSERT
        assertNotNull(result);
        verify(timeRepository, times(1)).pauseSession(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción si no hay sesión activa al pausar")
    void deberiaLanzarExcepcionSiNoHaySesionActivaAlPausar() {
        // ARRANGE
        when(timeRepository.findActiveSessionByUserEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(IllegalStateException.class,
            () -> pauseResumeTimeSessionUseCase.pauseSession(userEmail));
    }

    @Test
    @DisplayName("Debería lanzar excepción si la sesión ya está pausada")
    void deberiaLanzarExcepcionSiSesionYaPausada() {
        // ARRANGE
        activeSession.setPaused(true);
        when(timeRepository.findActiveSessionByUserEmail(anyString())).thenReturn(Optional.of(activeSession));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class,
            () -> pauseResumeTimeSessionUseCase.pauseSession(userEmail));

        verify(timeRepository, never()).pauseSession(anyLong());
    }

    @Test
    @DisplayName("Debería reanudar sesión pausada correctamente")
    void deberiaReanudarSesionPausada() {
        // ARRANGE
        activeSession.setPaused(true);
        when(timeRepository.findActiveSessionByUserEmail(anyString())).thenReturn(Optional.of(activeSession));
        when(timeRepository.resumeSession(anyLong())).thenReturn(Optional.of(activeSession));
        when(timeMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        TimeSessionOutputDto result = pauseResumeTimeSessionUseCase.resumeSession(userEmail);

        // ASSERT
        assertNotNull(result);
        verify(timeRepository, times(1)).resumeSession(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción si no hay sesión activa al reanudar")
    void deberiaLanzarExcepcionSiNoHaySesionActivaAlReanudar() {
        // ARRANGE
        when(timeRepository.findActiveSessionByUserEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(IllegalStateException.class,
            () -> pauseResumeTimeSessionUseCase.resumeSession(userEmail));
    }

    @Test
    @DisplayName("Debería lanzar excepción si la sesión no está pausada al reanudar")
    void deberiaLanzarExcepcionSiSesionNoEstaPausadaAlReanudar() {
        // ARRANGE
        activeSession.setPaused(false);
        when(timeRepository.findActiveSessionByUserEmail(anyString())).thenReturn(Optional.of(activeSession));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class,
            () -> pauseResumeTimeSessionUseCase.resumeSession(userEmail));

        verify(timeRepository, never()).resumeSession(anyLong());
    }
}
