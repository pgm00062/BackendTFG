package freelancer.backendtfg.application.port;

import freelancer.backendtfg.infrastructure.controller.dto.output.InvoiceOutputDto;

public interface GetInvoiceByIdUseCase {
    
    InvoiceOutputDto getInvoiceById(Long invoiceId, String userEmail);
} 