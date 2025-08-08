package freelancer.backendtfg.application.port.invoiceUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.input.invoicesInput.InvoiceCreateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.invoicesOutput.InvoiceOutputDto;

public interface CreateInvoiceUseCase {
    
    InvoiceOutputDto createInvoice(String userEmail, InvoiceCreateInputDto inputDto);
} 