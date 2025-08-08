package freelancer.backendtfg.application.useCaseImpl.invoiceUseCaseImpl;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import freelancer.backendtfg.infrastructure.repository.port.InvoiceRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.entity.InvoiceEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import java.util.Objects;
import freelancer.backendtfg.application.port.invoiceUseCasePort.DeleteInvoiceUseCase;

@Service
@RequiredArgsConstructor
public class DeleteInvoiceUseCaseImpl implements DeleteInvoiceUseCase {
    private final InvoiceRepositoryPort invoiceRepository;
    private final UserRepositoryPort userRepository;

    @Override
    public void deleteInvoice(Long invoiceId, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        InvoiceEntity invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
        if (!Objects.equals(invoice.getUser().getId(), user.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar esta factura");
        }
        invoiceRepository.delete(invoice);
    }
}
