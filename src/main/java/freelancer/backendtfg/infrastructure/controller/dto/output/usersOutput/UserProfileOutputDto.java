package freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO de salida con la información del perfil de usuario")
public class UserProfileOutputDto {

    @ApiModelProperty(value = "ID único del usuario", example = "7")
    private Long id;

    @ApiModelProperty(value = "Nombre del usuario", example = "Pablo")
    private String name;

    @ApiModelProperty(value = "Apellido del usuario", example = "García")
    private String surname;

    @ApiModelProperty(value = "Email del usuario", example = "pablo@example.com")
    private String email;

    @ApiModelProperty(value = "DNI del usuario", example = "12345678Z")
    private String dni;
} 