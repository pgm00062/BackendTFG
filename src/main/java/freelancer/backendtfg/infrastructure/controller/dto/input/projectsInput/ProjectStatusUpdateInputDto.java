package freelancer.backendtfg.infrastructure.controller.dto.input.projectsInput;

import freelancer.backendtfg.domain.enums.ProjectStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO para actualizar el estado de un proyecto")
public class ProjectStatusUpdateInputDto {

    @ApiModelProperty(
            value = "Estado del proyecto",
            required = true,
            example = "EN_PROGRESO",
            allowableValues = "EN_PROGRESO,COMPLETADO,CANCELADO" // ajusta según los valores de tu enum ProjectStatus
    )
    @NotNull(message = "El estado es obligatorio")
    private ProjectStatus status;
}