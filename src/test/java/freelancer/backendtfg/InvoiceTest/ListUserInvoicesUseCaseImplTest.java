package freelancer.backendtfg.InvoiceTest;

import freelancer.backendtfg.application.useCaseImpl.invoiceUseCaseImpl.ListUserInvoicesUseCaseImpl;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para ListUserInvoicesUseCaseImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Listar Facturas del Usuario")
class ListUserInvoicesUseCaseImplTest {

    @Mock
    private InvoiceRepositoryPort invoiceRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @InjectMocks
    private ListUserInvoicesUseCaseImpl listUserInvoicesUseCase;

    private String userEmail;
    private UserEntity userEntity;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";
        pageable = PageRequest.of(0, 10);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(userEmail);
    }

    @Test
    @DisplayName("Debería listar facturas del usuario paginadas")
    void deberiaListarFacturasPaginadas() {
        // ARRANGE
        InvoiceEntity invoice1 = new InvoiceEntity();
        invoice1.setId(1L);
        InvoiceEntity invoice2 = new InvoiceEntity();
        invoice2.setId(2L);
        List<InvoiceEntity> invoices = Arrays.asList(invoice1, invoice2);

        InvoiceOutputDto dto1 = new InvoiceOutputDto();
        dto1.setId(1L);
        InvoiceOutputDto dto2 = new InvoiceOutputDto();
        dto2.setId(2L);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(invoiceRepository.findByUserId(anyLong())).thenReturn(invoices);
        when(invoiceMapper.toOutputDto(invoice1)).thenReturn(dto1);
        when(invoiceMapper.toOutputDto(invoice2)).thenReturn(dto2);

        // ACT
        Page<InvoiceOutputDto> result = listUserInvoicesUseCase.listInvoices(userEmail, pageable);

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        verify(invoiceMapper, times(2)).toOutputDto(any(InvoiceEntity.class));
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class,
            () -> listUserInvoicesUseCase.listInvoices(userEmail, pageable));

        verify(invoiceRepository, never()).findByUserId(anyLong());
    }

    @Test
    @DisplayName("Debería retornar página vacía si el usuario no tiene facturas")
    void deberiaRetornarPaginaVaciaSiNoHayFacturas() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(invoiceRepository.findByUserId(anyLong())).thenReturn(Arrays.asList());

        // ACT
        Page<InvoiceOutputDto> result = listUserInvoicesUseCase.listInvoices(userEmail, pageable);

        // ASSERT
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debería paginar correctamente cuando hay más resultados que el tamaño de página")
    void deberiaPaginarCorrectamente() {
        // ARRANGE
        InvoiceEntity invoice1 = new InvoiceEntity();
        InvoiceEntity invoice2 = new InvoiceEntity();
        InvoiceEntity invoice3 = new InvoiceEntity();
        List<InvoiceEntity> invoices = Arrays.asList(invoice1, invoice2, invoice3);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(invoiceRepository.findByUserId(anyLong())).thenReturn(invoices);
        when(invoiceMapper.toOutputDto(any(InvoiceEntity.class))).thenReturn(new InvoiceOutputDto());

        Pageable pageableSize2 = PageRequest.of(0, 2);

        // ACT
        Page<InvoiceOutputDto> result = listUserInvoicesUseCase.listInvoices(userEmail, pageableSize2);

        // ASSERT
        assertNotNull(result);
        assertEquals(3, result.getTotalElements()); // Total de elementos
        assertEquals(2, result.getContent().size()); // Elementos en la página actual
    }

    @Test
    @DisplayName("Debería obtener segunda página correctamente")
    void deberiaObtenerSegundaPagina() {
        // ARRANGE
        InvoiceEntity invoice1 = new InvoiceEntity();
        InvoiceEntity invoice2 = new InvoiceEntity();
        InvoiceEntity invoice3 = new InvoiceEntity();
        List<InvoiceEntity> invoices = Arrays.asList(invoice1, invoice2, invoice3);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(invoiceRepository.findByUserId(anyLong())).thenReturn(invoices);
        when(invoiceMapper.toOutputDto(any(InvoiceEntity.class))).thenReturn(new InvoiceOutputDto());

        Pageable secondPage = PageRequest.of(1, 2);

        // ACT
        Page<InvoiceOutputDto> result = listUserInvoicesUseCase.listInvoices(userEmail, secondPage);

        // ASSERT
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getContent().size()); // Solo 1 elemento en la segunda página
    }
}
