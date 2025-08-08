package freelancer.backendtfg.infrastructure.controller.dto.input.timesInput;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO de entrada para iniciar una sesión de tiempo")
public class TimeStartInputDto {

    @ApiModelProperty(value = "ID del proyecto", required = true, example = "456")
    @NotNull(message = "El id del proyecto es obligatorio")
    private Long projectId;

    @ApiModelProperty(value = "Descripción opcional de la tarea a realizar", example = "Diseño de la interfaz de usuario")
    private String description;
} 