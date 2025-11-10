package freelancer.backendtfg.ProjectTest;

import freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl.GetStatiticsUseCaseImpl;
import freelancer.backendtfg.domain.enums.ProjectStatus;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para GetStatiticsUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear cálculo de estadísticas financieras de proyectos POR USUARIO
 * - Ganancias del último mes (proyectos TERMINADOS)
 * - Ganancias pendientes (proyectos EN_PROGRESO - sin filtro de fecha)
 * - Ganancias del año actual (proyectos TERMINADOS)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Obtener Estadísticas de Proyectos")
class GetStatiticsUseCaseImplTest {

    @Mock
    private ProjectRepositoryPort projectRepositoryPort;
    
    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private GetStatiticsUseCaseImpl getStatiticsUseCase;
    
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
    @DisplayName("Debería obtener ganancias del último mes de proyectos terminados del usuario")
    void deberiaObtenerGananciasDelUltimoMes() {
        // ARRANGE
        BigDecimal expectedEarnings = BigDecimal.valueOf(5000);
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(projectRepositoryPort.getTotalBudgetByStatusAndUser(
                any(ProjectStatus.class), eq(USER_ID), any(LocalDate.class)))
            .thenReturn(expectedEarnings);

        // ACT
        BigDecimal result = getStatiticsUseCase.getEarningsLastMonth(USER_EMAIL);

        // ASSERT
        assertNotNull(result);
        assertEquals(expectedEarnings, result);

        // Verificamos que se llamó con los parámetros correctos
        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(projectRepositoryPort, times(1)).getTotalBudgetByStatusAndUser(
            eq(ProjectStatus.TERMINADO), eq(USER_ID), dateCaptor.capture());

        // Verificamos que la fecha es aproximadamente 1 mes atrás
        LocalDate capturedDate = dateCaptor.getValue();
        LocalDate expectedDate = LocalDate.now().minusMonths(1);
        assertEquals(expectedDate, capturedDate);
    }

    @Test
    @DisplayName("Debería obtener ganancias pendientes de proyectos en progreso del usuario SIN filtro de fecha")
    void deberiaObtenerGananciasPendientes() {
        // ARRANGE
        BigDecimal expectedPending = BigDecimal.valueOf(7500);
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(projectRepositoryPort.getTotalBudgetByStatusAndUser(
                eq(ProjectStatus.EN_PROGRESO), eq(USER_ID), eq(null)))
            .thenReturn(expectedPending);

        // ACT
        BigDecimal result = getStatiticsUseCase.getPendingEarnings(USER_EMAIL);

        // ASSERT
        assertNotNull(result);
        assertEquals(expectedPending, result);

        // Verificamos que se llamó con EN_PROGRESO y SIN filtro de fecha (null)
        verify(projectRepositoryPort, times(1)).getTotalBudgetByStatusAndUser(
            eq(ProjectStatus.EN_PROGRESO), eq(USER_ID), eq(null));
    }

    @Test
    @DisplayName("Debería obtener ganancias del año actual de proyectos terminados del usuario")
    void deberiaObtenerGananciasDelAnioActual() {
        // ARRANGE
        BigDecimal expectedYearEarnings = BigDecimal.valueOf(50000);
        int currentYear = LocalDate.now().getYear();
        
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(projectRepositoryPort.getTotalBudgetByStatusUserAndYear(
                any(ProjectStatus.class), eq(USER_ID), anyInt()))
            .thenReturn(expectedYearEarnings);

        // ACT
        BigDecimal result = getStatiticsUseCase.getEarningsThisYear(USER_EMAIL);

        // ASSERT
        assertNotNull(result);
        assertEquals(expectedYearEarnings, result);

        // Verificamos que se llamó con el año actual y el userId
        verify(projectRepositoryPort, times(1)).getTotalBudgetByStatusUserAndYear(
            eq(ProjectStatus.TERMINADO), eq(USER_ID), eq(currentYear));
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe")
    void deberiaLanzarExcepcionSiUsuarioNoExiste() {
        // ARRANGE
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class, 
            () -> getStatiticsUseCase.getEarningsLastMonth(USER_EMAIL));
        assertThrows(UserNotFoundException.class, 
            () -> getStatiticsUseCase.getPendingEarnings(USER_EMAIL));
        assertThrows(UserNotFoundException.class, 
            () -> getStatiticsUseCase.getEarningsThisYear(USER_EMAIL));
    }

    @Test
    @DisplayName("Debería retornar BigDecimal.ZERO si no hay ganancias")
    void deberiaRetornarCeroCuandoNoHayGanancias() {
        // ARRANGE
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(projectRepositoryPort.getTotalBudgetByStatusAndUser(any(), eq(USER_ID), any()))
            .thenReturn(BigDecimal.ZERO);

        // ACT
        BigDecimal result = getStatiticsUseCase.getEarningsLastMonth(USER_EMAIL);

        // ASSERT
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Debería usar la fecha actual para calcular el mes anterior")
    void deberiaUsarFechaActualParaCalcularMesAnterior() {
        // ARRANGE
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(projectRepositoryPort.getTotalBudgetByStatusAndUser(any(), eq(USER_ID), any()))
            .thenReturn(BigDecimal.ZERO);

        // ACT
        getStatiticsUseCase.getEarningsLastMonth(USER_EMAIL);

        // ASSERT
        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(projectRepositoryPort).getTotalBudgetByStatusAndUser(any(), eq(USER_ID), dateCaptor.capture());

        LocalDate capturedDate = dateCaptor.getValue();
        LocalDate now = LocalDate.now();
        
        // La fecha capturada debería ser aproximadamente 1 mes antes de ahora
        assertTrue(capturedDate.isBefore(now), 
            "La fecha debería ser anterior a la fecha actual");
        assertTrue(capturedDate.isAfter(now.minusMonths(2)), 
            "La fecha no debería ser más de 2 meses atrás");
    }

    @Test
    @DisplayName("Debería filtrar por estado TERMINADO y userId para ganancias del último mes")
    void deberiaFiltrarPorEstadoTerminadoParaGananciasDelMes() {
        // ARRANGE
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(projectRepositoryPort.getTotalBudgetByStatusAndUser(any(), eq(USER_ID), any()))
            .thenReturn(BigDecimal.ZERO);

        // ACT
        getStatiticsUseCase.getEarningsLastMonth(USER_EMAIL);

        // ASSERT
        verify(projectRepositoryPort, times(1)).getTotalBudgetByStatusAndUser(
            eq(ProjectStatus.TERMINADO), eq(USER_ID), any());
    }

    @Test
    @DisplayName("Debería filtrar por estado EN_PROGRESO y userId para ganancias pendientes")
    void deberiaFiltrarPorEstadoEnProgresoParaGananciasPendientes() {
        // ARRANGE
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(projectRepositoryPort.getTotalBudgetByStatusAndUser(any(), eq(USER_ID), any()))
            .thenReturn(BigDecimal.ZERO);

        // ACT
        getStatiticsUseCase.getPendingEarnings(USER_EMAIL);

        // ASSERT
        verify(projectRepositoryPort, times(1)).getTotalBudgetByStatusAndUser(
            eq(ProjectStatus.EN_PROGRESO), eq(USER_ID), eq(null));
    }

    @Test
    @DisplayName("Debería usar el año actual y userId al calcular ganancias del año")
    void deberiaUsarAnioActualParaGananciasDelAnio() {
        // ARRANGE
        int currentYear = LocalDate.now().getYear();
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(projectRepositoryPort.getTotalBudgetByStatusUserAndYear(any(), eq(USER_ID), anyInt()))
            .thenReturn(BigDecimal.ZERO);

        // ACT
        getStatiticsUseCase.getEarningsThisYear(USER_EMAIL);

        // ASSERT
        verify(projectRepositoryPort, times(1)).getTotalBudgetByStatusUserAndYear(
            any(), eq(USER_ID), eq(currentYear));
    }

    @Test
    @DisplayName("Debería manejar correctamente valores grandes de BigDecimal")
    void deberiaManejiarValoresGrandesDeBigDecimal() {
        // ARRANGE
        BigDecimal largeAmount = new BigDecimal("999999999.99");
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(projectRepositoryPort.getTotalBudgetByStatusAndUser(any(), eq(USER_ID), any()))
            .thenReturn(largeAmount);

        // ACT
        BigDecimal result = getStatiticsUseCase.getEarningsLastMonth(USER_EMAIL);

        // ASSERT
        assertEquals(largeAmount, result);
    }

    @Test
    @DisplayName("Debería llamar al repositorio exactamente una vez por cada método de estadística")
    void deberiaLlamarAlRepositorioUnaVezPorMetodo() {
        // ARRANGE
        when(userRepositoryPort.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));
        when(projectRepositoryPort.getTotalBudgetByStatusAndUser(any(), eq(USER_ID), any()))
            .thenReturn(BigDecimal.ZERO);
        when(projectRepositoryPort.getTotalBudgetByStatusUserAndYear(any(), eq(USER_ID), anyInt()))
            .thenReturn(BigDecimal.ZERO);

        // ACT
        getStatiticsUseCase.getEarningsLastMonth(USER_EMAIL);
        getStatiticsUseCase.getPendingEarnings(USER_EMAIL);
        getStatiticsUseCase.getEarningsThisYear(USER_EMAIL);

        // ASSERT
        verify(projectRepositoryPort, times(2)).getTotalBudgetByStatusAndUser(any(), eq(USER_ID), any());
        verify(projectRepositoryPort, times(1)).getTotalBudgetByStatusUserAndYear(any(), eq(USER_ID), anyInt());
        verify(userRepositoryPort, times(3)).findByEmail(USER_EMAIL);
    }
}
