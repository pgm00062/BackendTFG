package freelancer.backendtfg.TimeTest;

import freelancer.backendtfg.application.useCaseImpl.timeUseCaseImpl.GetProjectTotalTimeUseCaseImpl;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectTotalTimeOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionDailyOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
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
 * Tests Unitarios para GetProjectTotalTimeUseCaseImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Obtener Tiempo Total de Proyecto")
class GetProjectTotalTimeUseCaseImplTest {

    @Mock
    private TimeRepositoryPort timeRepository;

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private GetProjectTotalTimeUseCaseImpl getProjectTotalTimeUseCase;

    private String userEmail;
    private Long projectId;
    private UserEntity userEntity;
    private ProjectEntity projectEntity;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";
        projectId = 1L;

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Pablo");
        userEntity.setSurname("García");
        userEntity.setEmail(userEmail);

        projectEntity = new ProjectEntity();
        projectEntity.setId(projectId);
        projectEntity.setName("Proyecto Web");
    }

    @Test
    @DisplayName("Debería calcular tiempo total del proyecto")
    void deberiaCalcularTiempoTotalDelProyecto() {
        // ARRANGE
        TimeEntity session1 = createCompletedSession(120); // 2 horas
        TimeEntity session2 = createCompletedSession(90);  // 1.5 horas
        List<TimeEntity> sessions = Arrays.asList(session1, session2);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(timeRepository.findCompletedSessionsByProjectIdAndUserEmail(anyLong(), anyString()))
            .thenReturn(sessions);

        // ACT
        ProjectTotalTimeOutputDto result = getProjectTotalTimeUseCase.getProjectTotalTime(userEmail, projectId);

        // ASSERT
        assertNotNull(result);
        assertEquals(210, result.getTotalMinutes()); // 120 + 90
        assertEquals(3.5, result.getTotalHours(), 0.1); // 210/60
        assertEquals(2, result.getTotalSessions());
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class,
            () -> getProjectTotalTimeUseCase.getProjectTotalTime(userEmail, projectId));
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el proyecto no existe")
    void deberiaLanzarExcepcionCuandoProyectoNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class,
            () -> getProjectTotalTimeUseCase.getProjectTotalTime(userEmail, projectId));
    }

    @Test
    @DisplayName("Debería obtener tiempo total diario")
    void deberiaObtenerTiempoTotalDiario() {
        // ARRANGE
        TimeEntity session = createCompletedSession(120);
        List<TimeEntity> sessions = Arrays.asList(session);

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59);

        when(timeRepository.findCompletedSessionsByUserEmailAndDate(anyString(), any(), any()))
            .thenReturn(sessions);

        // ACT
        TimeSessionDailyOutputDto result = getProjectTotalTimeUseCase.getDailyTotalTime(
            userEmail, startOfDay, endOfDay);

        // ASSERT
        assertNotNull(result);
        assertEquals(120, result.getTotalMinutes());
        assertEquals(2.0, result.getTotalHours(), 0.1);
    }

    @Test
    @DisplayName("Debería obtener horas totales como Double")
    void deberiaObtenerHorasTotalesComoDouble() {
        // ARRANGE
        TimeEntity session = createCompletedSession(150); // 2.5 horas
        List<TimeEntity> sessions = Arrays.asList(session);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(timeRepository.findCompletedSessionsByProjectIdAndUserEmail(anyLong(), anyString()))
            .thenReturn(sessions);

        // ACT
        Double result = getProjectTotalTimeUseCase.getTotalHoursForProject(userEmail, projectId);

        // ASSERT
        assertNotNull(result);
        assertEquals(2.5, result, 0.1);
    }

    private TimeEntity createCompletedSession(int durationMinutes) {
        TimeEntity session = new TimeEntity();
        LocalDateTime start = LocalDateTime.now().minusMinutes(durationMinutes);
        session.setStartTime(start);
        session.setEndTime(LocalDateTime.now());
        session.setActive(false);
        return session;
    }
}
