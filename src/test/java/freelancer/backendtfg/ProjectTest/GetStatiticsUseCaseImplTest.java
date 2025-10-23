package freelancer.backendtfg.ProjectTest;

import freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl.GetStatiticsUseCaseImpl;
import freelancer.backendtfg.domain.enums.ProjectStatus;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para GetStatiticsUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear cálculo de estadísticas financieras de proyectos
 * - Ganancias del último mes (proyectos TERMINADOS)
 * - Ganancias pendientes (proyectos EN_PROGRESO)
 * - Ganancias del año actual (proyectos TERMINADOS)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Obtener Estadísticas de Proyectos")
class GetStatiticsUseCaseImplTest {

    @Mock
    private ProjectRepositoryPort projectRepositoryPort;

    @InjectMocks
    private GetStatiticsUseCaseImpl getStatiticsUseCase;

    @BeforeEach
    void setUp() {
        // No necesitamos configuración especial aquí
    }

    @Test
    @DisplayName("Debería obtener ganancias del último mes de proyectos terminados")
    void deberiaObtenerGananciasDelUltimoMes() {
        // ARRANGE
        BigDecimal expectedEarnings = BigDecimal.valueOf(5000);
        when(projectRepositoryPort.getTotalBudgetByStatus(any(ProjectStatus.class), any(LocalDate.class)))
            .thenReturn(expectedEarnings);

        // ACT
        BigDecimal result = getStatiticsUseCase.getEarningsLastMonth();

        // ASSERT
        assertNotNull(result);
        assertEquals(expectedEarnings, result);

        // Verificamos que se llamó con los parámetros correctos
        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(projectRepositoryPort, times(1)).getTotalBudgetByStatus(
            eq(ProjectStatus.TERMINADO), dateCaptor.capture());

        // Verificamos que la fecha es aproximadamente 1 mes atrás
        LocalDate capturedDate = dateCaptor.getValue();
        LocalDate expectedDate = LocalDate.now().minusMonths(1);
        assertEquals(expectedDate, capturedDate);
    }

    @Test
    @DisplayName("Debería obtener ganancias pendientes de proyectos en progreso")
    void deberiaObtenerGananciasPendientes() {
        // ARRANGE
        BigDecimal expectedPending = BigDecimal.valueOf(7500);
        when(projectRepositoryPort.getTotalBudgetByStatus(any(ProjectStatus.class), any(LocalDate.class)))
            .thenReturn(expectedPending);

        // ACT
        BigDecimal result = getStatiticsUseCase.getPendingEarnings();

        // ASSERT
        assertNotNull(result);
        assertEquals(expectedPending, result);

        // Verificamos que se llamó con EN_PROGRESO
        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(projectRepositoryPort, times(1)).getTotalBudgetByStatus(
            eq(ProjectStatus.EN_PROGRESO), dateCaptor.capture());

        // Verificamos que usa la misma lógica de fecha (1 mes atrás)
        LocalDate capturedDate = dateCaptor.getValue();
        LocalDate expectedDate = LocalDate.now().minusMonths(1);
        assertEquals(expectedDate, capturedDate);
    }

    @Test
    @DisplayName("Debería obtener ganancias del año actual de proyectos terminados")
    void deberiaObtenerGananciasDelAnioActual() {
        // ARRANGE
        BigDecimal expectedYearEarnings = BigDecimal.valueOf(50000);
        int currentYear = LocalDate.now().getYear();
        
        when(projectRepositoryPort.getTotalBudgetByStatusAndYear(any(ProjectStatus.class), anyInt()))
            .thenReturn(expectedYearEarnings);

        // ACT
        BigDecimal result = getStatiticsUseCase.getEarningsThisYear();

        // ASSERT
        assertNotNull(result);
        assertEquals(expectedYearEarnings, result);

        // Verificamos que se llamó con el año actual
        verify(projectRepositoryPort, times(1)).getTotalBudgetByStatusAndYear(
            eq(ProjectStatus.TERMINADO), eq(currentYear));
    }

    @Test
    @DisplayName("Debería retornar BigDecimal.ZERO si no hay ganancias")
    void deberiaRetornarCeroCuandoNoHayGanancias() {
        // ARRANGE
        when(projectRepositoryPort.getTotalBudgetByStatus(any(), any())).thenReturn(BigDecimal.ZERO);

        // ACT
        BigDecimal result = getStatiticsUseCase.getEarningsLastMonth();

        // ASSERT
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Debería usar la fecha actual para calcular el mes anterior")
    void deberiaUsarFechaActualParaCalcularMesAnterior() {
        // ARRANGE
        when(projectRepositoryPort.getTotalBudgetByStatus(any(), any())).thenReturn(BigDecimal.ZERO);

        // ACT
        getStatiticsUseCase.getEarningsLastMonth();

        // ASSERT
        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(projectRepositoryPort).getTotalBudgetByStatus(any(), dateCaptor.capture());

        LocalDate capturedDate = dateCaptor.getValue();
        LocalDate now = LocalDate.now();
        
        // La fecha capturada debería ser aproximadamente 1 mes antes de ahora
        assertTrue(capturedDate.isBefore(now), 
            "La fecha debería ser anterior a la fecha actual");
        assertTrue(capturedDate.isAfter(now.minusMonths(2)), 
            "La fecha no debería ser más de 2 meses atrás");
    }

    @Test
    @DisplayName("Debería filtrar por estado TERMINADO para ganancias del último mes")
    void deberiaFiltrarPorEstadoTerminadoParaGananciasDelMes() {
        // ARRANGE
        when(projectRepositoryPort.getTotalBudgetByStatus(any(), any())).thenReturn(BigDecimal.ZERO);

        // ACT
        getStatiticsUseCase.getEarningsLastMonth();

        // ASSERT
        verify(projectRepositoryPort, times(1)).getTotalBudgetByStatus(
            eq(ProjectStatus.TERMINADO), any());
    }

    @Test
    @DisplayName("Debería filtrar por estado EN_PROGRESO para ganancias pendientes")
    void deberiaFiltrarPorEstadoEnProgresoParaGananciasPendientes() {
        // ARRANGE
        when(projectRepositoryPort.getTotalBudgetByStatus(any(), any())).thenReturn(BigDecimal.ZERO);

        // ACT
        getStatiticsUseCase.getPendingEarnings();

        // ASSERT
        verify(projectRepositoryPort, times(1)).getTotalBudgetByStatus(
            eq(ProjectStatus.EN_PROGRESO), any());
    }

    @Test
    @DisplayName("Debería usar el año actual al calcular ganancias del año")
    void deberiaUsarAnioActualParaGananciasDelAnio() {
        // ARRANGE
        int currentYear = LocalDate.now().getYear();
        when(projectRepositoryPort.getTotalBudgetByStatusAndYear(any(), anyInt()))
            .thenReturn(BigDecimal.ZERO);

        // ACT
        getStatiticsUseCase.getEarningsThisYear();

        // ASSERT
        verify(projectRepositoryPort, times(1)).getTotalBudgetByStatusAndYear(
            any(), eq(currentYear));
    }

    @Test
    @DisplayName("Debería manejar correctamente valores grandes de BigDecimal")
    void deberiaManejiarValoresGrandesDeBigDecimal() {
        // ARRANGE
        BigDecimal largeAmount = new BigDecimal("999999999.99");
        when(projectRepositoryPort.getTotalBudgetByStatus(any(), any())).thenReturn(largeAmount);

        // ACT
        BigDecimal result = getStatiticsUseCase.getEarningsLastMonth();

        // ASSERT
        assertEquals(largeAmount, result);
    }

    @Test
    @DisplayName("Debería llamar al repositorio exactamente una vez por cada método de estadística")
    void deberiaLlamarAlRepositorioUnaVezPorMetodo() {
        // ARRANGE
        when(projectRepositoryPort.getTotalBudgetByStatus(any(), any())).thenReturn(BigDecimal.ZERO);
        when(projectRepositoryPort.getTotalBudgetByStatusAndYear(any(), anyInt())).thenReturn(BigDecimal.ZERO);

        // ACT
        getStatiticsUseCase.getEarningsLastMonth();
        getStatiticsUseCase.getPendingEarnings();
        getStatiticsUseCase.getEarningsThisYear();

        // ASSERT
        verify(projectRepositoryPort, times(2)).getTotalBudgetByStatus(any(), any());
        verify(projectRepositoryPort, times(1)).getTotalBudgetByStatusAndYear(any(), anyInt());
    }
}
