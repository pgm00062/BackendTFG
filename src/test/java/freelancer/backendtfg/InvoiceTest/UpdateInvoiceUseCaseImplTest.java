package freelancer.backendtfg.InvoiceTest;

import freelancer.backendtfg.application.useCaseImpl.invoiceUseCaseImpl.UpdateInvoiceUseCaseImpl;
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
 * Tests Unitarios para UpdateInvoiceUseCaseImpl
 * 
 * REGLA DE NEGOCIO CRÍTICA:
 * - NO se pueden modificar facturas en estado SENT (enviada) o PAID (pagada)
 * - Solo se pueden editar facturas en DRAFT (borrador)
 * - Los totales se recalculan automáticamente al actualizar
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Actualizar Factura")
class UpdateInvoiceUseCaseImplTest {

    @Mock
    private InvoiceRepositoryPort invoiceRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @InjectMocks
    private UpdateInvoiceUseCaseImpl updateInvoiceUseCase;

    private String userEmail;
    private Long invoiceId;
    private UserEntity userEntity;
    private ProjectEntity projectEntity;
    private InvoiceEntity existingInvoice;
    private InvoiceCreateInputDto inputDto;
    private InvoiceOutputDto outputDto;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";
        invoiceId = 1L;

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(userEmail);

        projectEntity = new ProjectEntity();
        projectEntity.setId(1L);
        projectEntity.setUser(userEntity);

        existingInvoice = new InvoiceEntity();
        existingInvoice.setId(invoiceId);
        existingInvoice.setUser(userEntity);
        existingInvoice.setStatus(InvoiceStatus.DRAFT);

        inputDto = new InvoiceCreateInputDto();
        inputDto.setProjectId(1L);
        inputDto.setIssueDate(LocalDate.now());
        inputDto.setDueDate(LocalDate.now().plusDays(30));
        inputDto.setHourlyRate(new BigDecimal("60.00"));
        inputDto.setTotalHours(new BigDecimal("15"));
        inputDto.setProjectBudget(new BigDecimal("1500.00"));
        inputDto.setAdditionalCosts(new BigDecimal("200.00"));
        inputDto.setTaxRate(new BigDecimal("21"));
        inputDto.setClientName("Cliente Actualizado");

        outputDto = new InvoiceOutputDto();
        outputDto.setId(invoiceId);
    }

    @Test
    @DisplayName("Debería actualizar factura en estado DRAFT exitosamente")
    void deberiaActualizarFacturaDraft() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(existingInvoice));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenReturn(existingInvoice);
        when(invoiceMapper.toOutputDto(any(InvoiceEntity.class))).thenReturn(outputDto);

        // ACT
        InvoiceOutputDto result = updateInvoiceUseCase.updateInvoice(invoiceId, userEmail, inputDto);

        // ASSERT
        assertNotNull(result);
        verify(invoiceRepository, times(1)).save(any(InvoiceEntity.class));
    }

    @Test
    @DisplayName("Debería recalcular totales al actualizar")
    void deberiaRecalcularTotales() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(existingInvoice));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenReturn(existingInvoice);
        when(invoiceMapper.toOutputDto(any(InvoiceEntity.class))).thenReturn(outputDto);

        ArgumentCaptor<InvoiceEntity> captor = ArgumentCaptor.forClass(InvoiceEntity.class);

        // ACT
        updateInvoiceUseCase.updateInvoice(invoiceId, userEmail, inputDto);

        // ASSERT
        verify(invoiceRepository).save(captor.capture());
        InvoiceEntity savedInvoice = captor.getValue();

        assertEquals(new BigDecimal("900.00"), savedInvoice.getTimeCost());
        
        assertEquals(new BigDecimal("2600.00"), savedInvoice.getSubtotal());
        
        assertEquals(new BigDecimal("546.0000"), savedInvoice.getTaxAmount());
        
        assertEquals(new BigDecimal("3146.0000"), savedInvoice.getTotalAmount());
    }

    @Test
    @DisplayName("Debería actualizar todos los campos de la factura")
    void deberiaActualizarTodosLosCampos() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(existingInvoice));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenReturn(existingInvoice);
        when(invoiceMapper.toOutputDto(any(InvoiceEntity.class))).thenReturn(outputDto);

        ArgumentCaptor<InvoiceEntity> captor = ArgumentCaptor.forClass(InvoiceEntity.class);

        // ACT
        updateInvoiceUseCase.updateInvoice(invoiceId, userEmail, inputDto);

        // ASSERT
        verify(invoiceRepository).save(captor.capture());
        InvoiceEntity savedInvoice = captor.getValue();

        assertEquals(inputDto.getIssueDate(), savedInvoice.getIssueDate());
        assertEquals(inputDto.getDueDate(), savedInvoice.getDueDate());
        assertEquals(inputDto.getHourlyRate(), savedInvoice.getHourlyRate());
        assertEquals(inputDto.getClientName(), savedInvoice.getClientName());
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException cuando la factura está SENT")
    void deberiaLanzarExcepcionCuandoFacturaEnviada() {
        // ARRANGE
        existingInvoice.setStatus(InvoiceStatus.SENT);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(existingInvoice));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> updateInvoiceUseCase.updateInvoice(invoiceId, userEmail, inputDto));

        assertTrue(exception.getMessage().contains("enviada o pagada"));
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException cuando la factura está PAID")
    void deberiaLanzarExcepcionCuandoFacturaPagada() {
        // ARRANGE
        existingInvoice.setStatus(InvoiceStatus.PAID);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(existingInvoice));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> updateInvoiceUseCase.updateInvoice(invoiceId, userEmail, inputDto));

        assertTrue(exception.getMessage().contains("enviada o pagada"));
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class,
            () -> updateInvoiceUseCase.updateInvoice(invoiceId, userEmail, inputDto));

        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException cuando la factura no existe")
    void deberiaLanzarExcepcionCuandoFacturaNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(RuntimeException.class,
            () -> updateInvoiceUseCase.updateInvoice(invoiceId, userEmail, inputDto));

        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException cuando la factura no pertenece al usuario")
    void deberiaLanzarExcepcionCuandoFacturaNoPertenece() {
        // ARRANGE
        UserEntity otroUsuario = new UserEntity();
        otroUsuario.setId(999L);
        existingInvoice.setUser(otroUsuario);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(existingInvoice));

        // ACT & ASSERT
        assertThrows(RuntimeException.class,
            () -> updateInvoiceUseCase.updateInvoice(invoiceId, userEmail, inputDto));

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
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(existingInvoice));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));

        // ACT & ASSERT
        assertThrows(RuntimeException.class,
            () -> updateInvoiceUseCase.updateInvoice(invoiceId, userEmail, inputDto));

        verify(invoiceRepository, never()).save(any());
    }
}
