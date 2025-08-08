package freelancer.backendtfg.infrastructure.controller.dto.input.usersInput;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO de entrada para el login de usuario")
public class UserLoginInputDto {

    @ApiModelProperty(value = "Email del usuario", required = true, example = "usuario@example.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @ApiModelProperty(value = "Contraseña del usuario", required = true, example = "miContraseña123")
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
