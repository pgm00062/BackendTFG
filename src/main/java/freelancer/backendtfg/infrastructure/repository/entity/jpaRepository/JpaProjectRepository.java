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
import java.util.List;

public interface JpaProjectRepository extends JpaRepository<ProjectEntity, Long> {
    Page<ProjectEntity> findByUserId(Long userId, Pageable pageable);
    Page<ProjectEntity> findByUserIdAndNameContainingIgnoreCase(Long userId, String name, Pageable pageable);
    
    // Query para ganancias del último mes (con filtro de usuario y fecha)
    @Query("SELECT COALESCE(SUM(p.budget), 0) FROM ProjectEntity p " +
           "WHERE p.status = :status AND p.user.id = :userId AND p.endDate >= :fromDate")
    BigDecimal getTotalBudgetByStatusUserAndDateRange(
            @Param("status") ProjectStatus status, 
            @Param("userId") Long userId, 
            @Param("fromDate") LocalDate fromDate);
    
    // Query para ganancias pendientes (sin filtro de fecha)
    @Query("SELECT COALESCE(SUM(p.budget), 0) FROM ProjectEntity p " +
           "WHERE p.status = :status AND p.user.id = :userId")
    BigDecimal getTotalBudgetByStatusAndUser(
            @Param("status") ProjectStatus status, 
            @Param("userId") Long userId);
    
    // Query para ganancias del año actual (con filtro de usuario y año)
    @Query("SELECT COALESCE(SUM(p.budget), 0) FROM ProjectEntity p " +
           "WHERE p.status = :status AND p.user.id = :userId AND YEAR(p.endDate) = :year")
    BigDecimal getTotalBudgetByStatusUserAndYear(
            @Param("status") ProjectStatus status, 
            @Param("userId") Long userId, 
            @Param("year") int year);
    
    Page<ProjectEntity> findByUserIdAndStatus(Long id, ProjectStatus status, Pageable pageable);
    List<ProjectEntity> findTop3ByUserIdOrderByStartDateDesc(Long userId);
}