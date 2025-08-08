package freelancer.backendtfg.infrastructure.controller.dto.input.timesInput;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO de entrada para finalizar una sesión de tiempo")
public class TimeEndInputDto {

    @ApiModelProperty(value = "ID de la sesión de tiempo", required = true, example = "123")
    @NotNull(message = "El ID de la sesión de tiempo es obligatorio")
    private Long timeSessionId;

    @ApiModelProperty(value = "Descripción opcional de lo realizado", example = "Desarrollo del módulo de autenticación")
    private String description;
}