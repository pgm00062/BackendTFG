package freelancer.backendtfg.application.port.invoiceUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.output.invoicesOutput.InvoiceOutputDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListUserInvoicesUseCase {
    
    Page<InvoiceOutputDto> listInvoices(String userEmail, Pageable pageable);
} 