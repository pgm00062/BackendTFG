package freelancer.backendtfg.infrastructure.controller.dto.input.usersInput;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO de entrada para actualizar el perfil de usuario")
public class UpdateUserProfileInputDto {

    @ApiModelProperty(value = "Nombre del usuario", required = true, example = "Pablo")
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @ApiModelProperty(value = "Apellido del usuario", required = true, example = "García")
    @NotBlank(message = "El apellido es obligatorio")
    private String surname;

    @ApiModelProperty(value = "DNI del usuario", required = true, example = "12345678Z")
    @NotBlank(message = "El DNI es obligatorio")
    private String dni;
}