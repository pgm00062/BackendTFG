package freelancer.backendtfg.infrastructure.repository;

import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import freelancer.backendtfg.infrastructure.repository.port.JpaTimeRepository;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TimeRepository implements TimeRepositoryPort {
    private final JpaTimeRepository jpaRepository;

    @Override
    public TimeEntity save(TimeEntity time) {
        return jpaRepository.save(time);
    }

    @Override
    public Optional<TimeEntity> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<TimeEntity> findActiveSessionByUserEmail(String userEmail) {
        return jpaRepository.findByUserEmailAndIsActiveTrue(userEmail);
    }

    @Override
    public Page<TimeEntity> findByUserEmail(String userEmail, Pageable pageable) {
        return jpaRepository.findByUserEmailOrderByStartTimeDesc(userEmail, pageable);
    }

    @Override
    public Optional<TimeEntity> findActiveSessionByUserEmailAndProjectId(String userEmail, Long projectId) {
        return jpaRepository.findByUserEmailAndProjectIdAndIsActiveTrue(userEmail, projectId);
    }

    @Override
    public boolean existsActiveSessionByUserEmail(String userEmail) {
        return jpaRepository.existsByUserEmailAndIsActiveTrue(userEmail);
    }

    @Override
    public long countByUserEmail(String userEmail) {
        return jpaRepository.countByUserEmail(userEmail);
    }

    @Override
    public Page<TimeEntity> findByProjectId(Long projectId, Pageable pageable) {
        return jpaRepository.findByProjectIdOrderByStartTimeDesc(projectId, pageable);
    }

    @Override
    public List<TimeEntity> findByProjectIdAndUserEmail(Long projectId, String userEmail) {
        return jpaRepository.findByProjectIdAndUserEmail(projectId, userEmail);
    }

    @Override
    public List<TimeEntity> findCompletedSessionsByProjectIdAndUserEmail(Long projectId, String userEmail) {
        return jpaRepository.findCompletedSessionsByProjectIdAndUserEmail(projectId, userEmail);
    }

    @Override
    public long countByProjectIdAndUserEmail(Long projectId, String userEmail) {
        return jpaRepository.countByProjectIdAndUserEmail(projectId, userEmail);
    }
} 