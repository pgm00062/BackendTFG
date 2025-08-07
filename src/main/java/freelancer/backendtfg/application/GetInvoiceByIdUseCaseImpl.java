package freelancer.backendtfg.application;

import freelancer.backendtfg.application.port.GetInvoiceByIdUseCase;
import freelancer.backendtfg.domain.mapper.InvoiceMapper;
import freelancer.backendtfg.infrastructure.controller.dto.output.InvoiceOutputDto;
import freelancer.backendtfg.infrastructure.repository.port.InvoiceRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.entity.InvoiceEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetInvoiceByIdUseCaseImpl implements GetInvoiceByIdUseCase {

    private final InvoiceRepositoryPort invoiceRepository;
    private final UserRepositoryPort userRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    public InvoiceOutputDto getInvoiceById(Long invoiceId, String userEmail) {
        // Validar que el usuario existe
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // Validar que la factura existe y pertenece al usuario
        InvoiceEntity invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        if (!invoice.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("La factura no pertenece al usuario");
        }

        // Retornar el DTO de salida
        return invoiceMapper.toOutputDto(invoice);
    }
} 