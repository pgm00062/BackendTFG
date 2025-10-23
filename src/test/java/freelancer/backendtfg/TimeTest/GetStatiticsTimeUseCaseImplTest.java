package freelancer.backendtfg.TimeTest;

import freelancer.backendtfg.application.useCaseImpl.timeUseCaseImpl.GetStatiticsTimeUseCaseImpl;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionDailyOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para GetStatiticsTimeUseCaseImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Obtener Estadísticas de Tiempo")
class GetStatiticsTimeUseCaseImplTest {

    @Mock
    private TimeRepositoryPort timeRepository;

    @InjectMocks
    private GetStatiticsTimeUseCaseImpl getStatiticsTimeUseCase;

    private String userEmail;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";
    }

    @Test
    @DisplayName("Debería obtener tiempo total del último mes")
    void deberiaObtenerTiempoTotalDelUltimoMes() {
        // ARRANGE
        TimeEntity session1 = createSessionWithDuration(120);
        TimeEntity session2 = createSessionWithDuration(90);
        List<TimeEntity> sessions = Arrays.asList(session1, session2);

        when(timeRepository.findCompletedSessionsByUserEmailAndDateRange(anyString(), any(), any()))
            .thenReturn(sessions);

        // ACT
        TimeSessionDailyOutputDto result = getStatiticsTimeUseCase.getTotalTimeLastMonth(userEmail);

        // ASSERT
        assertNotNull(result);
        assertEquals(210, result.getTotalMinutes()); // 120 + 90
        assertEquals(3.5, result.getTotalHours(), 0.1); // 210/60
    }

    @Test
    @DisplayName("Debería obtener tiempo total del año actual")
    void deberiaObtenerTiempoTotalDelAnioActual() {
        // ARRANGE
        TimeEntity session1 = createSessionWithDuration(180);
        TimeEntity session2 = createSessionWithDuration(120);
        List<TimeEntity> sessions = Arrays.asList(session1, session2);

        when(timeRepository.findCompletedSessionsByUserEmailAndYear(anyString(), anyInt()))
            .thenReturn(sessions);

        // ACT
        TimeSessionDailyOutputDto result = getStatiticsTimeUseCase.getTotalTimeThisYear(userEmail);

        // ASSERT
        assertNotNull(result);
        assertEquals(300, result.getTotalMinutes()); // 180 + 120
        assertEquals(5.0, result.getTotalHours(), 0.1); // 300/60
    }

    @Test
    @DisplayName("Debería retornar cero si no hay sesiones en el último mes")
    void deberiaRetornarCeroSiNoHaySesionesEnElMes() {
        // ARRANGE
        when(timeRepository.findCompletedSessionsByUserEmailAndDateRange(anyString(), any(), any()))
            .thenReturn(Arrays.asList());

        // ACT
        TimeSessionDailyOutputDto result = getStatiticsTimeUseCase.getTotalTimeLastMonth(userEmail);

        // ASSERT
        assertNotNull(result);
        assertEquals(0, result.getTotalMinutes());
        assertEquals(0.0, result.getTotalHours());
    }

    @Test
    @DisplayName("Debería retornar cero si no hay sesiones en el año")
    void deberiaRetornarCeroSiNoHaySesionesEnElAnio() {
        // ARRANGE
        when(timeRepository.findCompletedSessionsByUserEmailAndYear(anyString(), anyInt()))
            .thenReturn(Arrays.asList());

        // ACT
        TimeSessionDailyOutputDto result = getStatiticsTimeUseCase.getTotalTimeThisYear(userEmail);

        // ASSERT
        assertNotNull(result);
        assertEquals(0, result.getTotalMinutes());
        assertEquals(0.0, result.getTotalHours());
    }

    @Test
    @DisplayName("Debería filtrar por año actual correctamente")
    void deberiaFiltrarPorAnioActualCorrectamente() {
        // ARRANGE
        int currentYear = LocalDateTime.now().getYear();
        when(timeRepository.findCompletedSessionsByUserEmailAndYear(anyString(), anyInt()))
            .thenReturn(Arrays.asList());

        // ACT
        getStatiticsTimeUseCase.getTotalTimeThisYear(userEmail);

        // ASSERT
        verify(timeRepository, times(1)).findCompletedSessionsByUserEmailAndYear(
            eq(userEmail), eq(currentYear));
    }

    private TimeEntity createSessionWithDuration(long minutes) {
        TimeEntity session = mock(TimeEntity.class);
        when(session.getDurationInMinutes()).thenReturn(minutes);
        return session;
    }
}
