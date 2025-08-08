package freelancer.backendtfg.infrastructure.controller.dto.input.usersInput;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO de entrada para el cambio de contraseña del usuario")
public class ChangePasswordInputDto {

    @ApiModelProperty(value = "Contraseña antigua", example = "miContrasena123")
    @NotBlank(message = "La contraseña actual es obligatoria")
    private String oldPassword;

    @ApiModelProperty(value = "Contraseña nueva", example = "miNuevaContrasena123")
    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
    private String newPassword;
} 