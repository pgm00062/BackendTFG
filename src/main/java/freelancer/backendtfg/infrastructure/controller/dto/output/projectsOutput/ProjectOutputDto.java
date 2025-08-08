package freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput;

import freelancer.backendtfg.domain.enums.ProjectType;
import freelancer.backendtfg.domain.enums.ProjectStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO de salida que representa un proyecto")
public class ProjectOutputDto {

    @ApiModelProperty(value = "ID único del proyecto", example = "15")
    private Long id;

    @ApiModelProperty(value = "Nombre del proyecto", example = "Proyecto Alpha")
    private String name;

    @ApiModelProperty(value = "Descripción del proyecto", example = "Desarrollo de una aplicación web para gestión de tareas")
    private String description;

    @ApiModelProperty(value = "Tipo de proyecto", example = "DESARROLLO")
    private ProjectType type;

    @ApiModelProperty(value = "Fecha de inicio del proyecto", example = "2025-01-10")
    private LocalDate startDate;

    @ApiModelProperty(value = "Fecha de fin estimada del proyecto", example = "2025-06-30")
    private LocalDate endDate;

    @ApiModelProperty(value = "Presupuesto asignado al proyecto", example = "50000.00")
    private BigDecimal budget;

    @ApiModelProperty(value = "Estado actual del proyecto", example = "EN_PROGRESO")
    private ProjectStatus status;
} 