package freelancer.backendtfg.infrastructure.repository.port;

import freelancer.backendtfg.domain.enums.ProjectStatus;
import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface ProjectRepositoryPort {
    Optional<ProjectEntity> findById(Long id);
    Page<ProjectEntity> findByUserId(Long userId, Pageable pageable);
    Page<ProjectEntity> findByUserIdAndNameContainingIgnoreCase(Long userId, String name, Pageable pageable);
    ProjectEntity save(ProjectEntity project);
    void delete(ProjectEntity project);
    BigDecimal getTotalBudgetByStatus(ProjectStatus status, LocalDate fromDate);
    Page<ProjectEntity> findByUserIdAndStatus(Long id, ProjectStatus status, Pageable pageable);
} 