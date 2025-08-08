package freelancer.backendtfg.infrastructure.controller.dto.input.usersInput;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO de entrada para el registro de un usuario")
public class UserRegisterInputDto {

    @ApiModelProperty(value = "Nombre del usuario", required = true, example = "Pablo")
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @ApiModelProperty(value = "Apellido del usuario", required = true, example = "García")
    @NotBlank(message = "El apellido es obligatorio")
    private String surname;

    @ApiModelProperty(value = "Email del usuario", required = true, example = "pablo.garcia@example.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    @ApiModelProperty(value = "DNI del usuario", required = true, example = "12345678Z")
    @NotBlank(message = "El DNI es obligatorio")
    private String dni;

    @ApiModelProperty(value = "Contraseña del usuario", required = true, example = "MiContraseña123")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
}