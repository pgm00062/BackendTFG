package freelancer.backendtfg.application.port;

public interface DeleteInvoiceUseCase {
    void deleteInvoice(Long invoiceId, String userEmail);
}