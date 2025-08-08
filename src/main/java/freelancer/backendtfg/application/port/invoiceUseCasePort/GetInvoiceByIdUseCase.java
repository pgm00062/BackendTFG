package freelancer.backendtfg.application.port.invoiceUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.output.invoicesOutput.InvoiceOutputDto;

public interface GetInvoiceByIdUseCase {
    
    InvoiceOutputDto getInvoiceById(Long invoiceId, String userEmail);
} 