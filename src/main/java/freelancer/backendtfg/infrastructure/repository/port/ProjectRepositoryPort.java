package freelancer.backendtfg.infrastructure.repository.port;

import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ProjectRepositoryPort {
    Optional<ProjectEntity> findById(Long id);
    Page<ProjectEntity> findByUserId(Long userId, Pageable pageable);
    Page<ProjectEntity> findByUserIdAndNameContainingIgnoreCase(Long userId, String name, Pageable pageable);
    ProjectEntity save(ProjectEntity project);
    void delete(ProjectEntity project);
} 