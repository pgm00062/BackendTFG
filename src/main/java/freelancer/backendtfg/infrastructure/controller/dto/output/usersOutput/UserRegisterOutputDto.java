package freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO de salida tras el registro exitoso de un usuario")
public class UserRegisterOutputDto {

    @ApiModelProperty(value = "ID único del usuario registrado", example = "7")
    private Long id;

    @ApiModelProperty(value = "Nombre del usuario registrado", example = "Pablo")
    private String name;

    @ApiModelProperty(value = "Email del usuario registrado", example = "pablo@example.com")
    private String email;
}
