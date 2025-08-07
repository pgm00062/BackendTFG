package freelancer.backendtfg.application;

import freelancer.backendtfg.application.port.ListUserInvoicesUseCase;
import freelancer.backendtfg.domain.mapper.InvoiceMapper;
import freelancer.backendtfg.infrastructure.controller.dto.output.InvoiceOutputDto;
import freelancer.backendtfg.infrastructure.repository.port.InvoiceRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.entity.InvoiceEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListUserInvoicesUseCaseImpl implements ListUserInvoicesUseCase {

    private final InvoiceRepositoryPort invoiceRepository;
    private final UserRepositoryPort userRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    public Page<InvoiceOutputDto> listInvoices(String userEmail, Pageable pageable) {
        // Validar que el usuario existe
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // Obtener las facturas del usuario
        List<InvoiceEntity> invoices = invoiceRepository.findByUserId(user.getId());
        
        // Convertir a DTOs
        List<InvoiceOutputDto> invoiceDtos = invoices.stream()
                .map(invoiceMapper::toOutputDto)
                .collect(Collectors.toList());
        
        // Crear una página con los resultados
        // Nota: Para una implementación más eficiente, se debería usar paginación en el repositorio
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), invoiceDtos.size());
        
        List<InvoiceOutputDto> pageContent = invoiceDtos.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(
                pageContent, 
                pageable, 
                invoiceDtos.size()
        );
    }
} 