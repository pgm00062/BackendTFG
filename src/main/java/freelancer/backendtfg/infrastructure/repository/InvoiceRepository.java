package freelancer.backendtfg.infrastructure.repository;

import freelancer.backendtfg.infrastructure.repository.entity.InvoiceEntity;
import freelancer.backendtfg.infrastructure.repository.entity.jpaRepository.JpaInvoiceRepository;
import freelancer.backendtfg.infrastructure.repository.port.InvoiceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InvoiceRepository implements InvoiceRepositoryPort {

    private final JpaInvoiceRepository jpaInvoiceRepository;

    @Override
    public InvoiceEntity save(InvoiceEntity invoice) {
        return jpaInvoiceRepository.save(invoice);
    }

    @Override
    public Optional<InvoiceEntity> findById(Long id) {
        return jpaInvoiceRepository.findById(id);
    }

    @Override
    public List<InvoiceEntity> findByUserId(Long userId) {
        return jpaInvoiceRepository.findByUserId(userId);
    }

    @Override
    public List<InvoiceEntity> findByProjectId(Long projectId) {
        return jpaInvoiceRepository.findByProjectId(projectId);
    }

    @Override
    public List<InvoiceEntity> findByUserIdAndProjectId(Long userId, Long projectId) {
        return jpaInvoiceRepository.findByUserIdAndProjectId(userId, projectId);
    }

    @Override
    public Optional<InvoiceEntity> findByInvoiceNumber(String invoiceNumber) {
        return jpaInvoiceRepository.findByInvoiceNumber(invoiceNumber);
    }

    @Override
    public List<InvoiceEntity> findByUserIdAndStatus(Long userId, String status) {
        return jpaInvoiceRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    public boolean existsByInvoiceNumber(String invoiceNumber) {
        return jpaInvoiceRepository.existsByInvoiceNumber(invoiceNumber);
    }


    @Override
    public long countByUserId(Long userId) {
        return jpaInvoiceRepository.countByUserId(userId);
    }

    @Override
    public long countByProjectId(Long projectId) {
        return jpaInvoiceRepository.countByProjectId(projectId);
    }

    @Override
    public void delete(InvoiceEntity invoice) {
        jpaInvoiceRepository.delete(invoice);
    }
} 