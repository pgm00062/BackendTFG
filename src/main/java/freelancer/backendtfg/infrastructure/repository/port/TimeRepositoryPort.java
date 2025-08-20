package freelancer.backendtfg.infrastructure.repository.port;

import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TimeRepositoryPort {
    TimeEntity save(TimeEntity time);
    Optional<TimeEntity> findById(Long id);
    
    // Buscar sesión activa por usuario
    Optional<TimeEntity> findActiveSessionByUserEmail(String userEmail);
    
    // Listar todas las sesiones de un usuario
    Page<TimeEntity> findByUserEmail(String userEmail, Pageable pageable);
    
    // Buscar sesión activa por usuario y proyecto
    Optional<TimeEntity> findActiveSessionByUserEmailAndProjectId(String userEmail, Long projectId);
    
    // Verificar si existe una sesión activa para un usuario
    boolean existsActiveSessionByUserEmail(String userEmail);
    
    // Contar sesiones por usuario
    long countByUserEmail(String userEmail);
    
    // Buscar sesiones por proyecto
    Page<TimeEntity> findByProjectId(Long projectId, Pageable pageable);
    
    // Nuevos métodos para calcular tiempo total por proyecto
    List<TimeEntity> findByProjectIdAndUserEmail(Long projectId, String userEmail);
    
    // Obtener todas las sesiones finalizadas de un proyecto para un usuario
    List<TimeEntity> findCompletedSessionsByProjectIdAndUserEmail(Long projectId, String userEmail);
    
    // Contar sesiones por proyecto y usuario
    long countByProjectIdAndUserEmail(Long projectId, String userEmail);

    void deleteByProjectId(Long projectId);

} 