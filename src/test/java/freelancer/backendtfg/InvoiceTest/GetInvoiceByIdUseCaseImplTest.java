package freelancer.backendtfg.InvoiceTest;

import freelancer.backendtfg.application.useCaseImpl.invoiceUseCaseImpl.GetInvoiceByIdUseCaseImpl;
import freelancer.backendtfg.domain.mapper.InvoiceMapper;
import freelancer.backendtfg.infrastructure.controller.dto.output.invoicesOutput.InvoiceOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.InvoiceEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.InvoiceRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
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
 * Tests Unitarios para GetInvoiceByIdUseCaseImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Obtener Factura por ID")
class GetInvoiceByIdUseCaseImplTest {

    @Mock
    private InvoiceRepositoryPort invoiceRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @InjectMocks
    private GetInvoiceByIdUseCaseImpl getInvoiceByIdUseCase;

    private String userEmail;
    private Long invoiceId;
    private UserEntity userEntity;
    private InvoiceEntity invoiceEntity;
    private InvoiceOutputDto outputDto;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";
        invoiceId = 1L;

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(userEmail);

        invoiceEntity = new InvoiceEntity();
        invoiceEntity.setId(invoiceId);
        invoiceEntity.setUser(userEntity);
        invoiceEntity.setInvoiceNumber("FAC-2025-10-001");

        outputDto = new InvoiceOutputDto();
        outputDto.setId(invoiceId);
        outputDto.setInvoiceNumber("FAC-2025-10-001");
    }

    @Test
    @DisplayName("Debería obtener factura por ID exitosamente")
    void deberiaObtenerFacturaPorId() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoiceEntity));
        when(invoiceMapper.toOutputDto(any(InvoiceEntity.class))).thenReturn(outputDto);

        // ACT
        InvoiceOutputDto result = getInvoiceByIdUseCase.getInvoiceById(invoiceId, userEmail);

        // ASSERT
        assertNotNull(result);
        assertEquals(invoiceId, result.getId());
        verify(invoiceMapper, times(1)).toOutputDto(invoiceEntity);
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class,
            () -> getInvoiceByIdUseCase.getInvoiceById(invoiceId, userEmail));

        verify(invoiceRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException cuando la factura no existe")
    void deberiaLanzarExcepcionCuandoFacturaNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(RuntimeException.class,
            () -> getInvoiceByIdUseCase.getInvoiceById(invoiceId, userEmail));

        verify(invoiceMapper, never()).toOutputDto(any());
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException cuando la factura no pertenece al usuario")
    void deberiaLanzarExcepcionCuandoFacturaNoPertenece() {
        // ARRANGE
        UserEntity otroUsuario = new UserEntity();
        otroUsuario.setId(999L);
        invoiceEntity.setUser(otroUsuario);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoiceEntity));

        // ACT & ASSERT
        assertThrows(RuntimeException.class,
            () -> getInvoiceByIdUseCase.getInvoiceById(invoiceId, userEmail));

        verify(invoiceMapper, never()).toOutputDto(any());
    }
}
