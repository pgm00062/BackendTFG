package freelancer.backendtfg.application.port.invoiceUseCasePort;

public interface DeleteInvoiceUseCase {
    void deleteInvoice(Long invoiceId, String userEmail);
}