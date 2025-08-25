package freelancer.backendtfg.infrastructure.repository.entity.jpaRepository;

import freelancer.backendtfg.domain.enums.ProjectStatus;
import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface JpaProjectRepository extends JpaRepository<ProjectEntity, Long> {
    Page<ProjectEntity> findByUserId(Long userId, Pageable pageable);
    Page<ProjectEntity> findByUserIdAndNameContainingIgnoreCase(Long userId, String name, Pageable pageable);
    @Query("SELECT COALESCE(SUM(p.budget), 0) FROM ProjectEntity p WHERE p.status = :status AND p.endDate >= :fromDate")
    BigDecimal getTotalBudgetByStatusAndDateRange(@Param("status") ProjectStatus status, @Param("fromDate") LocalDate fromDate);
    Page<ProjectEntity> findByUserIdAndStatus(Long id, ProjectStatus status, Pageable pageable);
}