package freelancer.backendtfg.infrastructure.repository;

import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import freelancer.backendtfg.infrastructure.repository.port.JpaProjectRepository;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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
} 