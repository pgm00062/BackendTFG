package freelancer.backendtfg.infrastructure.controller.dto.input;

import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordInputDto {
    @NotBlank(message = "La contraseña actual es obligatoria")
    private String oldPassword;
    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
    private String newPassword;
} 