package freelancer.backendtfg.ProjectTest;

import freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl.GetEarningsRateUseCaseImpl;
import freelancer.backendtfg.domain.enums.ProjectStatus;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.EarningsRateSummaryOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectEarningsRateOutputDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para GetEarningsRateUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear cálculo de tasa de ganancia por hora (€/hora)
 * - Validar cálculo individual por proyecto
 * - Validar media general de todos los proyectos
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Obtener Tasa de Ganancia por Hora")
class GetEarningsRateUseCaseImplTest {

    @Mock
    private ProjectRepositoryPort projectRepositoryPort;
    
    @Mock
    private TimeRepositoryPort timeRepositoryPort;
    
    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private GetEarningsRateUseCaseImpl getEarningsRateUseCase;
    
    private static final String USER_EMAIL = "test@example.com";
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
    @DisplayName("Debería calcular correctamente la tasa de ganancia para un proyecto")
    void deberiaCalcularTasaDeGananciaCorrectamente() {
        // ARRANGE
        ProjectEntity project = createProject(1L, "Proyecto A", new BigDecimal("1000.00"));
        List<TimeEntity> timeSessions = Arrays.asList(
            createTimeSessionWithHours(10.0),  // 10 horas
            createTimeSessionWithHours(5.0)    // 5 horas
        );
        
        Page<ProjectEntity> projectsPage = new PageImpl<>(Arrays.asList(project));
        
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(projectRepositoryPort.findByUserId(eq(USER_ID), any(PageRequest.class)))
            .thenReturn(projectsPage);
        when(timeRepositoryPort.findCompletedSessionsByProjectIdAndUserEmail(1L, USER_EMAIL))
            .thenReturn(timeSessions);

        // ACT
        EarningsRateSummaryOutputDto result = getEarningsRateUseCase.getEarningsRate(USER_EMAIL);

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.getTotalProjects());
        assertEquals(15.0, result.getTotalHours(), 0.01); // 10 + 5
        assertEquals(new BigDecimal("1000.00"), result.getTotalBudget());
        // 1000 / 15 = 66.67
        assertEquals(new BigDecimal("66.67"), result.getAverageEarningsPerHour());
        
        assertEquals(1, result.getProjectRates().size());
        ProjectEarningsRateOutputDto projectRate = result.getProjectRates().get(0);
        assertEquals("Proyecto A", projectRate.getProjectName());
        assertEquals(new BigDecimal("66.67"), projectRate.getEarningsPerHour());
    }

    @Test
    @DisplayName("Debería calcular la media correcta con múltiples proyectos")
    void deberiaCalcularMediaConMultiplesProyectos() {
        // ARRANGE
        ProjectEntity project1 = createProject(1L, "Proyecto A", new BigDecimal("1000.00"));
        ProjectEntity project2 = createProject(2L, "Proyecto B", new BigDecimal("2000.00"));
        
        List<TimeEntity> timeSessions1 = Arrays.asList(createTimeSessionWithHours(10.0));
        List<TimeEntity> timeSessions2 = Arrays.asList(createTimeSessionWithHours(20.0));
        
        Page<ProjectEntity> projectsPage = new PageImpl<>(Arrays.asList(project1, project2));
        
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(projectRepositoryPort.findByUserId(eq(USER_ID), any(PageRequest.class)))
            .thenReturn(projectsPage);
        when(timeRepositoryPort.findCompletedSessionsByProjectIdAndUserEmail(1L, USER_EMAIL))
            .thenReturn(timeSessions1);
        when(timeRepositoryPort.findCompletedSessionsByProjectIdAndUserEmail(2L, USER_EMAIL))
            .thenReturn(timeSessions2);

        // ACT
        EarningsRateSummaryOutputDto result = getEarningsRateUseCase.getEarningsRate(USER_EMAIL);

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.getTotalProjects());
        assertEquals(30.0, result.getTotalHours(), 0.01); // 10 + 20
        assertEquals(new BigDecimal("3000.00"), result.getTotalBudget());
        // 3000 / 30 = 100.00
        assertEquals(new BigDecimal("100.00"), result.getAverageEarningsPerHour());
    }

    @Test
    @DisplayName("Debería retornar cero si no hay horas trabajadas")
    void deberiaRetornarCeroSiNoHayHorasTrabajadas() {
        // ARRANGE
        ProjectEntity project = createProject(1L, "Proyecto A", new BigDecimal("1000.00"));
        
        Page<ProjectEntity> projectsPage = new PageImpl<>(Arrays.asList(project));
        
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(projectRepositoryPort.findByUserId(eq(USER_ID), any(PageRequest.class)))
            .thenReturn(projectsPage);
        when(timeRepositoryPort.findCompletedSessionsByProjectIdAndUserEmail(1L, USER_EMAIL))
            .thenReturn(Arrays.asList()); // Sin sesiones

        // ACT
        EarningsRateSummaryOutputDto result = getEarningsRateUseCase.getEarningsRate(USER_EMAIL);

        // ASSERT
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getAverageEarningsPerHour());
        assertEquals(0.0, result.getTotalHours());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe")
    void deberiaLanzarExcepcionSiUsuarioNoExiste() {
        // ARRANGE
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class, 
            () -> getEarningsRateUseCase.getEarningsRate(USER_EMAIL));
        
        verify(userRepositoryPort, times(1)).findByEmail(USER_EMAIL);
        verify(projectRepositoryPort, never()).findByUserId(anyLong(), any());
    }

    @Test
    @DisplayName("Debería manejar proyectos sin sesiones de tiempo")
    void deberiaManejarProyectosSinSesiones() {
        // ARRANGE
        ProjectEntity project1 = createProject(1L, "Proyecto A", new BigDecimal("1000.00"));
        ProjectEntity project2 = createProject(2L, "Proyecto B", new BigDecimal("2000.00"));
        
        List<TimeEntity> timeSessions = Arrays.asList(createTimeSessionWithHours(10.0));
        
        Page<ProjectEntity> projectsPage = new PageImpl<>(Arrays.asList(project1, project2));
        
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(projectRepositoryPort.findByUserId(eq(USER_ID), any(PageRequest.class)))
            .thenReturn(projectsPage);
        when(timeRepositoryPort.findCompletedSessionsByProjectIdAndUserEmail(1L, USER_EMAIL))
            .thenReturn(timeSessions);
        when(timeRepositoryPort.findCompletedSessionsByProjectIdAndUserEmail(2L, USER_EMAIL))
            .thenReturn(Arrays.asList()); // Sin sesiones

        // ACT
        EarningsRateSummaryOutputDto result = getEarningsRateUseCase.getEarningsRate(USER_EMAIL);

        // ASSERT
        assertEquals(2, result.getTotalProjects());
        assertEquals(2, result.getProjectRates().size());
        
        // Proyecto sin horas debe tener tasa 0
        ProjectEarningsRateOutputDto project2Rate = result.getProjectRates().stream()
            .filter(p -> p.getProjectId() == 2L)
            .findFirst()
            .orElse(null);
        assertNotNull(project2Rate);
        assertEquals(BigDecimal.ZERO, project2Rate.getEarningsPerHour());
    }

    @Test
    @DisplayName("Debería retornar lista vacía si no hay proyectos")
    void deberiaRetornarListaVaciaSiNoHayProyectos() {
        // ARRANGE
        Page<ProjectEntity> projectsPage = new PageImpl<>(Arrays.asList());
        
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(projectRepositoryPort.findByUserId(eq(USER_ID), any(PageRequest.class)))
            .thenReturn(projectsPage);

        // ACT
        EarningsRateSummaryOutputDto result = getEarningsRateUseCase.getEarningsRate(USER_EMAIL);

        // ASSERT
        assertNotNull(result);
        assertEquals(0, result.getTotalProjects());
        assertEquals(0.0, result.getTotalHours());
        assertEquals(BigDecimal.ZERO, result.getTotalBudget());
        assertEquals(BigDecimal.ZERO, result.getAverageEarningsPerHour());
        assertTrue(result.getProjectRates().isEmpty());
    }

    private ProjectEntity createProject(Long id, String name, BigDecimal budget) {
        ProjectEntity project = new ProjectEntity();
        project.setId(id);
        project.setName(name);
        project.setBudget(budget);
        project.setStatus(ProjectStatus.TERMINADO);
        project.setUser(testUser);
        return project;
    }

    private TimeEntity createTimeSessionWithHours(double hours) {
        TimeEntity session = mock(TimeEntity.class);
        when(session.getDurationInHours()).thenReturn(hours);
        return session;
    }
}
