package freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput;

import freelancer.backendtfg.domain.enums.ProjectStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO de salida que representa un proyecto")
public class ProjectOutputDtoParcial {
    @ApiModelProperty(value = "Nombre del proyecto", example = "Proyecto Alpha")
    private String name;

    @ApiModelProperty(value = "Descripción del proyecto", example = "Desarrollo de una aplicación web para gestión de tareas")
    private String description;

    @ApiModelProperty(value = "Estado actual del proyecto", example = "EN_PROGRESO")
    private ProjectStatus status;
}
