package freelancer.backendtfg.infrastructure.repository.entity.jpaRepository;

import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

public interface JpaTimeRepository extends JpaRepository<TimeEntity, Long> {
    
    // Buscar sesión activa por email de usuario
    @Query("SELECT t FROM TimeEntity t WHERE t.user.email = :userEmail AND t.isActive = true")
    Optional<TimeEntity> findByUserEmailAndIsActiveTrue(@Param("userEmail") String userEmail);
    
    // Listar sesiones de un usuario ordenadas por fecha de inicio descendente
    @Query("SELECT t FROM TimeEntity t WHERE t.user.email = :userEmail ORDER BY t.startTime DESC")
    Page<TimeEntity> findByUserEmailOrderByStartTimeDesc(@Param("userEmail") String userEmail, Pageable pageable);
    
    // Buscar sesión activa por usuario y proyecto
    @Query("SELECT t FROM TimeEntity t WHERE t.user.email = :userEmail AND t.project.id = :projectId AND t.isActive = true")
    Optional<TimeEntity> findByUserEmailAndProjectIdAndIsActiveTrue(@Param("userEmail") String userEmail, @Param("projectId") Long projectId);
    
    // Verificar si existe sesión activa para un usuario
    @Query("SELECT COUNT(t) > 0 FROM TimeEntity t WHERE t.user.email = :userEmail AND t.isActive = true")
    boolean existsByUserEmailAndIsActiveTrue(@Param("userEmail") String userEmail);
    
    // Contar sesiones por usuario
    @Query("SELECT COUNT(t) FROM TimeEntity t WHERE t.user.email = :userEmail")
    long countByUserEmail(@Param("userEmail") String userEmail);
    
    // Buscar sesiones por proyecto ordenadas por fecha de inicio descendente
    @Query("SELECT t FROM TimeEntity t WHERE t.project.id = :projectId ORDER BY t.startTime DESC")
    Page<TimeEntity> findByProjectIdOrderByStartTimeDesc(@Param("projectId") Long projectId, Pageable pageable);
    
    // Nuevos métodos para calcular tiempo total por proyecto
    @Query("SELECT t FROM TimeEntity t WHERE t.project.id = :projectId AND t.user.email = :userEmail ORDER BY t.startTime DESC")
    List<TimeEntity> findByProjectIdAndUserEmail(@Param("projectId") Long projectId, @Param("userEmail") String userEmail);
    
    // Obtener todas las sesiones finalizadas de un proyecto para un usuario
    @Query("SELECT t FROM TimeEntity t WHERE t.project.id = :projectId AND t.user.email = :userEmail AND t.isActive = false AND t.endTime IS NOT NULL ORDER BY t.startTime DESC")
    List<TimeEntity> findCompletedSessionsByProjectIdAndUserEmail(@Param("projectId") Long projectId, @Param("userEmail") String userEmail);
    
    // Contar sesiones por proyecto y usuario
    @Query("SELECT COUNT(t) FROM TimeEntity t WHERE t.project.id = :projectId AND t.user.email = :userEmail")
    long countByProjectIdAndUserEmail(@Param("projectId") Long projectId, @Param("userEmail") String userEmail);

    @Modifying
    @Query("DELETE FROM TimeEntity t WHERE t.project.id = :projectId")
    void deleteByProjectId(@Param("projectId") Long projectId);
    
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE TimeEntity t SET t.isPaused = true, t.pausedAt = :now WHERE t.id = :id AND t.isActive = true AND t.isPaused = false")
    int pauseSession(@Param("id") Long id, @Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE TimeEntity t SET t.isPaused = false, t.pausedAt = null WHERE t.id = :id AND t.isActive = true AND t.isPaused = true")
    int resumeSession(@Param("id") Long id);

    @Query("SELECT t FROM TimeEntity t " +
        "WHERE t.user.email = :userEmail " +
        "AND t.startTime >= :startOfDay " +
        "AND t.startTime < :endOfDay " +
        "AND t.isActive = false")
    List<TimeEntity> findCompletedSessionsByUserEmailAndDate(
            @Param("userEmail") String userEmail,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);
            
    @Query("SELECT t FROM TimeEntity t WHERE t.user.email = :userEmail AND t.isActive = false AND t.endTime IS NOT NULL AND t.endTime >= :start AND t.endTime <= :end")
    List<TimeEntity> findCompletedSessionsByUserEmailAndDateRange(@Param("userEmail") String userEmail, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT t FROM TimeEntity t WHERE t.user.email = :userEmail AND t.isActive = false AND t.endTime IS NOT NULL AND YEAR(t.endTime) = :year")
    List<TimeEntity> findCompletedSessionsByUserEmailAndYear(@Param("userEmail") String userEmail, @Param("year") int year);
} 