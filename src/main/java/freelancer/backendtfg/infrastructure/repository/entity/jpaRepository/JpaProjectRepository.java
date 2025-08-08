package freelancer.backendtfg.infrastructure.repository.entity.jpaRepository;

import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JpaProjectRepository extends JpaRepository<ProjectEntity, Long> {
    Page<ProjectEntity> findByUserId(Long userId, Pageable pageable);
    Page<ProjectEntity> findByUserIdAndNameContainingIgnoreCase(Long userId, String name, Pageable pageable);
} 