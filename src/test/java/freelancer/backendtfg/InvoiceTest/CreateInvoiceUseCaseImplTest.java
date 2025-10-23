package freelancer.backendtfg.InvoiceTest;

import freelancer.backendtfg.application.useCaseImpl.invoiceUseCaseImpl.CreateInvoiceUseCaseImpl;
import freelancer.backendtfg.domain.enums.InvoiceStatus;
import freelancer.backendtfg.domain.mapper.InvoiceMapper;
import freelancer.backendtfg.infrastructure.controller.dto.input.invoicesInput.InvoiceCreateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.invoicesOutput.InvoiceOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.InvoiceEntity;
import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.InvoiceRepositoryPort;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para CreateInvoiceUseCaseImpl
 * 
 * PEDAGOGÍA:
 * - Las facturas son documentos financieros generados para proyectos
 * - Contienen cálculos automáticos: subtotal, impuestos, total
 * - El número de factura se genera automáticamente: FAC-YYYY-MM-XXX
 * - Estado por defecto: DRAFT (borrador)
 * - Validaciones: usuario existe, proyecto existe y pertenece al usuario
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Crear Factura")
class CreateInvoiceUseCaseImplTest {

    @Mock
    private InvoiceRepositoryPort invoiceRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @InjectMocks
    private CreateInvoiceUseCaseImpl createInvoiceUseCase;

    private String userEmail;
    private UserEntity userEntity;
    private ProjectEntity projectEntity;
    private InvoiceCreateInputDto inputDto;
    private InvoiceEntity invoiceEntity;
    private InvoiceOutputDto outputDto;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(userEmail);

        projectEntity = new ProjectEntity();
        projectEntity.setId(1L);
        projectEntity.setName("Proyecto Web");
        projectEntity.setUser(userEntity);

        inputDto = new InvoiceCreateInputDto();
        inputDto.setProjectId(1L);
        inputDto.setHourlyRate(new BigDecimal("50.00"));
        inputDto.setTotalHours(new BigDecimal("10"));
        inputDto.setProjectBudget(new BigDecimal("1000.00"));
        inputDto.setAdditionalCosts(new BigDecimal("100.00"));
        inputDto.setTaxRate(new BigDecimal("21"));
        inputDto.setClientName("Cliente Test");
        inputDto.setClientEmail("cliente@test.com");

        invoiceEntity = new InvoiceEntity();
        invoiceEntity.setId(1L);
        invoiceEntity.setHourlyRate(inputDto.getHourlyRate());
        invoiceEntity.setTotalHours(inputDto.getTotalHours());
        invoiceEntity.setProjectBudget(inputDto.getProjectBudget());
        invoiceEntity.setAdditionalCosts(inputDto.getAdditionalCosts());
        invoiceEntity.setTaxRate(inputDto.getTaxRate());

        outputDto = new InvoiceOutputDto();
        outputDto.setId(1L);
    }

    @Test
    @DisplayName("Debería crear una factura exitosamente")
    void deberiaCrearFacturaExitosamente() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(invoiceMapper.toEntity(any(InvoiceCreateInputDto.class))).thenReturn(invoiceEntity);
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenReturn(invoiceEntity);
        when(invoiceRepository.countByUserId(anyLong())).thenReturn(5L);
        when(invoiceMapper.toOutputDto(any(InvoiceEntity.class))).thenReturn(outputDto);

        // ACT
        InvoiceOutputDto result = createInvoiceUseCase.createInvoice(userEmail, inputDto);

        // ASSERT
        assertNotNull(result);
        verify(invoiceRepository, times(1)).save(any(InvoiceEntity.class));
    }

    @Test
    @DisplayName("Debería calcular correctamente los totales de la factura")
    void deberiaCalcularTotalesCorrectamente() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(invoiceMapper.toEntity(any(InvoiceCreateInputDto.class))).thenReturn(invoiceEntity);
        when(invoiceRepository.countByUserId(anyLong())).thenReturn(0L);
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenReturn(invoiceEntity);
        when(invoiceMapper.toOutputDto(any(InvoiceEntity.class))).thenReturn(outputDto);

        ArgumentCaptor<InvoiceEntity> captor = ArgumentCaptor.forClass(InvoiceEntity.class);

        // ACT
        createInvoiceUseCase.createInvoice(userEmail, inputDto);

        // ASSERT
        verify(invoiceRepository).save(captor.capture());
        InvoiceEntity savedInvoice = captor.getValue();

        // Verificar cálculos:
        // timeCost = 10 horas * 50€/h = 500€
        assertEquals(new BigDecimal("500.00"), savedInvoice.getTimeCost());
        
        // subtotal = 1000 (budget) + 500 (time) + 100 (additional) = 1600€
        assertEquals(new BigDecimal("1600.00"), savedInvoice.getSubtotal());
        
        // tax = 1600 * 21% = 336€
        assertEquals(new BigDecimal("336.0000"), savedInvoice.getTaxAmount());
        
        // total = 1600 + 336 = 1936€
        assertEquals(new BigDecimal("1936.0000"), savedInvoice.getTotalAmount());
    }

    @Test
    @DisplayName("Debería generar el número de factura automáticamente")
    void deberiaGenerarNumeroFacturaAutomaticamente() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(invoiceMapper.toEntity(any(InvoiceCreateInputDto.class))).thenReturn(invoiceEntity);
        when(invoiceRepository.countByUserId(anyLong())).thenReturn(5L);
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenReturn(invoiceEntity);
        when(invoiceMapper.toOutputDto(any(InvoiceEntity.class))).thenReturn(outputDto);

        ArgumentCaptor<InvoiceEntity> captor = ArgumentCaptor.forClass(InvoiceEntity.class);

        // ACT
        createInvoiceUseCase.createInvoice(userEmail, inputDto);

        // ASSERT
        verify(invoiceRepository).save(captor.capture());
        InvoiceEntity savedInvoice = captor.getValue();

        // Verificar formato: FAC-2025-10-006 (año actual, mes actual, secuencial)
        assertNotNull(savedInvoice.getInvoiceNumber());
        assertTrue(savedInvoice.getInvoiceNumber().startsWith("FAC-"));
        assertTrue(savedInvoice.getInvoiceNumber().contains(String.valueOf(LocalDate.now().getYear())));
    }

    @Test
    @DisplayName("Debería establecer estado DRAFT por defecto")
    void deberiaEstablecerEstadoDraftPorDefecto() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(invoiceMapper.toEntity(any(InvoiceCreateInputDto.class))).thenReturn(invoiceEntity);
        when(invoiceRepository.countByUserId(anyLong())).thenReturn(0L);
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenReturn(invoiceEntity);
        when(invoiceMapper.toOutputDto(any(InvoiceEntity.class))).thenReturn(outputDto);

        ArgumentCaptor<InvoiceEntity> captor = ArgumentCaptor.forClass(InvoiceEntity.class);

        // ACT
        createInvoiceUseCase.createInvoice(userEmail, inputDto);

        // ASSERT
        verify(invoiceRepository).save(captor.capture());
        assertEquals(InvoiceStatus.DRAFT, captor.getValue().getStatus());
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class,
            () -> createInvoiceUseCase.createInvoice(userEmail, inputDto));

        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException cuando el proyecto no existe")
    void deberiaLanzarExcepcionCuandoProyectoNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(RuntimeException.class,
            () -> createInvoiceUseCase.createInvoice(userEmail, inputDto));

        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException cuando el proyecto no pertenece al usuario")
    void deberiaLanzarExcepcionCuandoProyectoNoPertenece() {
        // ARRANGE
        UserEntity otroUsuario = new UserEntity();
        otroUsuario.setId(999L);
        projectEntity.setUser(otroUsuario);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));

        // ACT & ASSERT
        assertThrows(RuntimeException.class,
            () -> createInvoiceUseCase.createInvoice(userEmail, inputDto));

        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería calcular correctamente sin costos adicionales")
    void deberiaCalcularSinCostosAdicionales() {
        // ARRANGE
        inputDto.setAdditionalCosts(null);
        invoiceEntity.setAdditionalCosts(null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(invoiceMapper.toEntity(any(InvoiceCreateInputDto.class))).thenReturn(invoiceEntity);
        when(invoiceRepository.countByUserId(anyLong())).thenReturn(0L);
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenReturn(invoiceEntity);
        when(invoiceMapper.toOutputDto(any(InvoiceEntity.class))).thenReturn(outputDto);

        ArgumentCaptor<InvoiceEntity> captor = ArgumentCaptor.forClass(InvoiceEntity.class);

        // ACT
        createInvoiceUseCase.createInvoice(userEmail, inputDto);

        // ASSERT
        verify(invoiceRepository).save(captor.capture());
        InvoiceEntity savedInvoice = captor.getValue();

        // subtotal = 1000 (budget) + 500 (time) = 1500€
        assertEquals(new BigDecimal("1500.00"), savedInvoice.getSubtotal());
    }
}
