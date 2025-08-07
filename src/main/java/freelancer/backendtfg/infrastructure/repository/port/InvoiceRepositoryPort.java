package freelancer.backendtfg.infrastructure.repository.port;

import freelancer.backendtfg.infrastructure.repository.entity.InvoiceEntity;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepositoryPort {
    
    InvoiceEntity save(InvoiceEntity invoice);
    
    Optional<InvoiceEntity> findById(Long id);
    
    List<InvoiceEntity> findByUserId(Long userId);
    
    List<InvoiceEntity> findByProjectId(Long projectId);
    
    List<InvoiceEntity> findByUserIdAndProjectId(Long userId, Long projectId);
    
    Optional<InvoiceEntity> findByInvoiceNumber(String invoiceNumber);
    
    List<InvoiceEntity> findByUserIdAndStatus(Long userId, String status);
    
    boolean existsByInvoiceNumber(String invoiceNumber);
    
    void deleteById(Long id);

    void delete(InvoiceEntity invoice);
    
    long countByUserId(Long userId);
    
    long countByProjectId(Long projectId);
} 