package freelancer.backendtfg.infrastructure.controller.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSessionOutputDto {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isActive;
    private String description;
    private Long projectId;
    private String projectName;
    private Long userId;
    private String userName;
    
    // Campos calculados
    private Duration duration;
    private long durationInMinutes;
    private double durationInHours;
    private String formattedDuration; // Formato legible como "2h 30m"
    
    // Método para formatear la duración de manera legible
    public String getFormattedDuration() {
        if (duration == null) {
            return "0m";
        }
        
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }
} 