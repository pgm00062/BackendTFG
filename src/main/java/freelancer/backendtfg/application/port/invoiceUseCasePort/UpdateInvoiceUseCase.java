package freelancer.backendtfg.application.port.invoiceUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.input.invoicesInput.InvoiceCreateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.invoicesOutput.InvoiceOutputDto;

public interface UpdateInvoiceUseCase {
    
    InvoiceOutputDto updateInvoice(Long invoiceId, String userEmail, InvoiceCreateInputDto inputDto);
} 