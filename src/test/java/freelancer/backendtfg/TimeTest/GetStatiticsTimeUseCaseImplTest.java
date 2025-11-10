package freelancer.backendtfg.TimeTest;

import freelancer.backendtfg.application.useCaseImpl.timeUseCaseImpl.GetStatiticsTimeUseCaseImpl;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionDailyOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para GetStatiticsTimeUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear cálculo de estadísticas de tiempo de trabajo POR USUARIO
 * - Tiempo total del último mes (sesiones completadas)
 * - Tiempo total del año actual (sesiones completadas)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Obtener Estadísticas de Tiempo")
class GetStatiticsTimeUseCaseImplTest {

    @Mock
    private TimeRepositoryPort timeRepository;
    
    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private GetStatiticsTimeUseCaseImpl getStatiticsTimeUseCase;

    private static final String USER_EMAIL = "pablo@test.com";
    private static final Long USER_ID = 1L;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(USER_ID);
        testUser.setEmail(USER_EMAIL);
        testUser.setName("Test User");
    }

    @Test
    @DisplayName("Debería obtener tiempo total del último mes del usuario")
    void deberiaObtenerTiempoTotalDelUltimoMes() {
        // ARRANGE
        TimeEntity session1 = createSessionWithDuration(120); // 2 horas
        TimeEntity session2 = createSessionWithDuration(90);  // 1.5 horas
        List<TimeEntity> sessions = Arrays.asList(session1, session2);

        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(timeRepository.findCompletedSessionsByUserEmailAndDateRange(
                eq(USER_EMAIL), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(sessions);

        // ACT
        TimeSessionDailyOutputDto result = getStatiticsTimeUseCase.getTotalTimeLastMonth(USER_EMAIL);

        // ASSERT
        assertNotNull(result);
        assertEquals(210, result.getTotalMinutes()); // 120 + 90
        assertEquals(3.5, result.getTotalHours(), 0.1); // 210/60
        
        verify(userRepositoryPort, times(1)).findByEmail(USER_EMAIL);
        verify(timeRepository, times(1)).findCompletedSessionsByUserEmailAndDateRange(
                eq(USER_EMAIL), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Debería obtener tiempo total del año actual del usuario")
    void deberiaObtenerTiempoTotalDelAnioActual() {
        // ARRANGE
        TimeEntity session1 = createSessionWithDuration(180); // 3 horas
        TimeEntity session2 = createSessionWithDuration(120); // 2 horas
        List<TimeEntity> sessions = Arrays.asList(session1, session2);

        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(timeRepository.findCompletedSessionsByUserEmailAndYear(eq(USER_EMAIL), anyInt()))
            .thenReturn(sessions);

        // ACT
        TimeSessionDailyOutputDto result = getStatiticsTimeUseCase.getTotalTimeThisYear(USER_EMAIL);

        // ASSERT
        assertNotNull(result);
        assertEquals(300, result.getTotalMinutes()); // 180 + 120
        assertEquals(5.0, result.getTotalHours(), 0.1); // 300/60
        
        verify(userRepositoryPort, times(1)).findByEmail(USER_EMAIL);
        verify(timeRepository, times(1)).findCompletedSessionsByUserEmailAndYear(
                eq(USER_EMAIL), anyInt());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe al obtener tiempo del mes")
    void deberiaLanzarExcepcionSiUsuarioNoExisteEnMes() {
        // ARRANGE
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class, 
            () -> getStatiticsTimeUseCase.getTotalTimeLastMonth(USER_EMAIL));
        
        verify(userRepositoryPort, times(1)).findByEmail(USER_EMAIL);
        verify(timeRepository, never()).findCompletedSessionsByUserEmailAndDateRange(
                anyString(), any(), any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe al obtener tiempo del año")
    void deberiaLanzarExcepcionSiUsuarioNoExisteEnAnio() {
        // ARRANGE
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class, 
            () -> getStatiticsTimeUseCase.getTotalTimeThisYear(USER_EMAIL));
        
        verify(userRepositoryPort, times(1)).findByEmail(USER_EMAIL);
        verify(timeRepository, never()).findCompletedSessionsByUserEmailAndYear(
                anyString(), anyInt());
    }

    @Test
    @DisplayName("Debería retornar cero si no hay sesiones en el último mes")
    void deberiaRetornarCeroSiNoHaySesionesEnElMes() {
        // ARRANGE
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(timeRepository.findCompletedSessionsByUserEmailAndDateRange(
                eq(USER_EMAIL), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList());

        // ACT
        TimeSessionDailyOutputDto result = getStatiticsTimeUseCase.getTotalTimeLastMonth(USER_EMAIL);

        // ASSERT
        assertNotNull(result);
        assertEquals(0, result.getTotalMinutes());
        assertEquals(0.0, result.getTotalHours());
    }

    @Test
    @DisplayName("Debería retornar cero si no hay sesiones en el año")
    void deberiaRetornarCeroSiNoHaySesionesEnElAnio() {
        // ARRANGE
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(timeRepository.findCompletedSessionsByUserEmailAndYear(eq(USER_EMAIL), anyInt()))
            .thenReturn(Arrays.asList());

        // ACT
        TimeSessionDailyOutputDto result = getStatiticsTimeUseCase.getTotalTimeThisYear(USER_EMAIL);

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
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(timeRepository.findCompletedSessionsByUserEmailAndYear(eq(USER_EMAIL), anyInt()))
            .thenReturn(Arrays.asList());

        // ACT
        getStatiticsTimeUseCase.getTotalTimeThisYear(USER_EMAIL);

        // ASSERT
        verify(timeRepository, times(1)).findCompletedSessionsByUserEmailAndYear(
            eq(USER_EMAIL), eq(currentYear));
    }

    @Test
    @DisplayName("Debería calcular correctamente horas desde minutos")
    void deberiaCalcularCorrectamenteHorasDesdeMinutos() {
        // ARRANGE
        TimeEntity session = createSessionWithDuration(150); // 2.5 horas
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(timeRepository.findCompletedSessionsByUserEmailAndDateRange(
                eq(USER_EMAIL), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(session));

        // ACT
        TimeSessionDailyOutputDto result = getStatiticsTimeUseCase.getTotalTimeLastMonth(USER_EMAIL);

        // ASSERT
        assertEquals(150, result.getTotalMinutes());
        assertEquals(2.5, result.getTotalHours(), 0.01);
    }

    @Test
    @DisplayName("Debería sumar correctamente múltiples sesiones")
    void deberiaSumarCorrectamenteMultiplesSesiones() {
        // ARRANGE
        TimeEntity session1 = createSessionWithDuration(60);  // 1 hora
        TimeEntity session2 = createSessionWithDuration(45);  // 0.75 horas
        TimeEntity session3 = createSessionWithDuration(30);  // 0.5 horas
        List<TimeEntity> sessions = Arrays.asList(session1, session2, session3);

        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(timeRepository.findCompletedSessionsByUserEmailAndYear(eq(USER_EMAIL), anyInt()))
            .thenReturn(sessions);

        // ACT
        TimeSessionDailyOutputDto result = getStatiticsTimeUseCase.getTotalTimeThisYear(USER_EMAIL);

        // ASSERT
        assertEquals(135, result.getTotalMinutes()); // 60 + 45 + 30
        assertEquals(2.25, result.getTotalHours(), 0.01); // 135/60
    }

    private TimeEntity createSessionWithDuration(long minutes) {
        TimeEntity session = mock(TimeEntity.class);
        when(session.getDurationInMinutes()).thenReturn(minutes);
        return session;
    }
}
