package freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import javax.persistence.Column;

import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO de salida para una sesión de tiempo")
public class TimeSessionOutputDto {

    @ApiModelProperty(value = "ID único de la sesión de tiempo", example = "101")
    private Long id;

    @ApiModelProperty(value = "Fecha y hora de inicio de la sesión", example = "2025-08-07T09:00:00")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "Fecha y hora de fin de la sesión", example = "2025-08-07T11:30:00")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "Indica si la sesión está activa", example = "true")
    private boolean isActive;

    @ApiModelProperty(value = "Descripción de la sesión", example = "Desarrollo de funcionalidades para módulo de facturación")
    private String description;

    @ApiModelProperty(value = "ID del proyecto asociado", example = "15")
    private Long projectId;

    @ApiModelProperty(value = "Nombre del proyecto asociado", example = "Proyecto Alpha")
    private String projectName;

    @ApiModelProperty(value = "ID del usuario que realiza la sesión", example = "3")
    private Long userId;

    @ApiModelProperty(value = "Nombre del usuario que realiza la sesión", example = "Carlos Gómez")
    private String userName;

    // Campos calculados

    @ApiModelProperty(value = "Duración de la sesión como objeto Duration")
    private Duration duration;

    @ApiModelProperty(value = "Duración de la sesión en minutos", example = "150")
    private long durationInMinutes;

    @ApiModelProperty(value = "Duración de la sesión en horas decimales", example = "2.5")
    private double durationInHours;

    @ApiModelProperty(value = "Duración de la sesión formateada legiblemente", example = "2h 30m")
    private String formattedDuration;

    @ApiModelProperty(value = "Indica si la sesión de tiempo está pausada", example = "true")
    private boolean isPaused = false; 

    @ApiModelProperty(value = "La hora en la que la sesión de tiempo fue pausada", example = "2025-08-07T10:00:00")
    private LocalDateTime pausedAt;
    
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