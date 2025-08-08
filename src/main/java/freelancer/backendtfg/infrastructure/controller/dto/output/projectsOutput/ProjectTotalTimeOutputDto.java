package freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO de salida con el tiempo total y estadísticas de un proyecto")
public class ProjectTotalTimeOutputDto {

    @ApiModelProperty(value = "ID único del proyecto", example = "20")
    private Long projectId;

    @ApiModelProperty(value = "Nombre del proyecto", example = "Proyecto Beta")
    private String projectName;

    @ApiModelProperty(value = "ID del usuario propietario", example = "3")
    private Long userId;

    @ApiModelProperty(value = "Nombre del usuario propietario", example = "Laura Martínez")
    private String userName;

    // Tiempo total calculado
    @ApiModelProperty(value = "Duración total como objeto Duration")
    private Duration totalDuration;

    @ApiModelProperty(value = "Total de minutos trabajados", example = "125")
    private long totalMinutes;

    @ApiModelProperty(value = "Total de horas trabajadas (decimal)", example = "2.08")
    private double totalHours;

    @ApiModelProperty(value = "Tiempo total formateado", example = "2h 5m")
    private String formattedTotalTime;

    // Estadísticas adicionales
    @ApiModelProperty(value = "Número total de sesiones de tiempo", example = "7")
    private int totalSessions;

    @ApiModelProperty(value = "Tiempo promedio por sesión en minutos", example = "17.85")
    private double averageSessionTime;

    @ApiModelProperty(value = "Fecha de la última sesión", example = "2025-08-01")
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