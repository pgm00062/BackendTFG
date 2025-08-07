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

    // Método para calcular la duración de la sesión
    public Duration getDuration() {
        if (startTime == null) {
            return Duration.ZERO;
        }
        
        LocalDateTime end = (endTime != null) ? endTime : LocalDateTime.now();
        return Duration.between(startTime, end);
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
} 