package freelancer.backendtfg.application.port;

import freelancer.backendtfg.infrastructure.controller.dto.input.InvoiceCreateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.InvoiceOutputDto;

public interface CreateInvoiceUseCase {
    
    InvoiceOutputDto createInvoice(String userEmail, InvoiceCreateInputDto inputDto);
} 