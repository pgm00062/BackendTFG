package freelancer.backendtfg.infrastructure.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.Duration;

@Entity
@Table(name = "times")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = true) // Puede ser null si la sesión está activa
    private LocalDateTime endTime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private boolean isActive = true; // Indica si la sesión está activa

    @Column(length = 500)
    private String description; // Descripción opcional de la tarea realizada

    @Column(nullable = false)
    private boolean isPaused = false; // Indica si la sesión está pausada

    @Column(nullable = true)
    private LocalDateTime pausedAt; // Momento en que se pausó la sesión

    // Método para calcular la duración de la sesión
    public Duration getDuration() {
        if (startTime == null) {
            return Duration.ZERO;
        }
        LocalDateTime end = (endTime != null) ? endTime : LocalDateTime.now();
        Duration total = Duration.between(startTime, end);
        if (isPaused && pausedAt != null) {
            Duration pausedDuration = Duration.between(pausedAt, LocalDateTime.now());
            total = total.minus(pausedDuration);
        }
        return total;
    }

    // Método para obtener la duración en minutos
    public long getDurationInMinutes() {
        return getDuration().toMinutes();
    }

    // Método para obtener la duración en horas
    public double getDurationInHours() {
        return getDuration().toMinutes() / 60.0;
    }

    // Método para finalizar la sesión
    public void endSession() {
        this.endTime = LocalDateTime.now();
        this.isActive = false;
    }

    // Método para validar que la sesión puede ser finalizada
    public boolean canBeEnded() {
        return isActive && startTime != null && endTime == null;
    }

    // Método para validar que la sesión puede ser iniciada
    public boolean canBeStarted() {
        return !isActive || (startTime == null);
    }

    // Método para pausar la sesión
    public void pauseSession() {
        if (isActive && !isPaused) {
            this.isPaused = true;
            this.pausedAt = LocalDateTime.now();
        }
    }

    // Método para reanudar la sesión
    public void resumeSession() {
        if (isActive && isPaused) {
            this.isPaused = false;
            this.pausedAt = null;
        }
    }

    // Método para validar que la sesión puede ser pausada
    public boolean canBePaused() {
        return isActive && !isPaused;
    }

    // Método para validar que la sesión puede ser reanudada
    public boolean canBeResumed() {
        return isActive && isPaused;
    }
} 