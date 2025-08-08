package freelancer.backendtfg.infrastructure.repository.entity.jpaRepository;

import freelancer.backendtfg.infrastructure.repository.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaInvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
    
    List<InvoiceEntity> findByUserId(Long userId);
    
    List<InvoiceEntity> findByProjectId(Long projectId);
    
    List<InvoiceEntity> findByUserIdAndProjectId(Long userId, Long projectId);
    
    Optional<InvoiceEntity> findByInvoiceNumber(String invoiceNumber);
    
    List<InvoiceEntity> findByUserIdAndStatus(Long userId, String status);
    
    boolean existsByInvoiceNumber(String invoiceNumber);
    
    long countByUserId(Long userId);
    
    long countByProjectId(Long projectId);
} 