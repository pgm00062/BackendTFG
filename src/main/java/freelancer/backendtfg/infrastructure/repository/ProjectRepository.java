package freelancer.backendtfg.infrastructure.repository;

import freelancer.backendtfg.domain.enums.ProjectStatus;
import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import freelancer.backendtfg.infrastructure.repository.entity.jpaRepository.JpaProjectRepository;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectRepository implements ProjectRepositoryPort {
    private final JpaProjectRepository jpaRepository;

    @Override
    public Optional<ProjectEntity> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<ProjectEntity> findByUserId(Long userId, Pageable pageable) {
        return jpaRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<ProjectEntity> findByUserIdAndNameContainingIgnoreCase(Long userId, String name, Pageable pageable) {
        return jpaRepository.findByUserIdAndNameContainingIgnoreCase(userId, name, pageable);
    }

    @Override
    public ProjectEntity save(ProjectEntity project) {
        return jpaRepository.save(project);
    }

    @Override
    public void delete(ProjectEntity project) {
        jpaRepository.delete(project);
    }

    @Override
    public BigDecimal getTotalBudgetByStatus(ProjectStatus status, LocalDate fromDate){
        return jpaRepository.getTotalBudgetByStatusAndDateRange(status, fromDate);
    }

    @Override
    public Page<ProjectEntity> findByUserIdAndStatus(Long id, ProjectStatus status, Pageable pageable) {
        return jpaRepository.findByUserIdAndStatus(id, status, pageable);
    }

    @Override
    public List<ProjectEntity> findTop3ByUserIdOrderByCreatedAtDesc(Long userId) {
        return jpaRepository.findTop3ByUserIdOrderByStartDateDesc(userId);
    }
} 