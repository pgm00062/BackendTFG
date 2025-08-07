package freelancer.backendtfg.infrastructure.controller.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTotalTimeOutputDto {
    private Long projectId;
    private String projectName;
    private Long userId;
    private String userName;
    
    // Tiempo total calculado
    private Duration totalDuration;
    private long totalMinutes;
    private double totalHours;
    private String formattedTotalTime;
    
    // Estadísticas adicionales
    private int totalSessions;
    private double averageSessionTime; // en minutos
    private String lastSessionDate;
    
    // Método para formatear el tiempo total de manera legible
    public String getFormattedTotalTime() {
        if (totalDuration == null) {
            return "0m";
        }
        
        long hours = totalDuration.toHours();
        long minutes = totalDuration.toMinutes() % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }
} 