package freelancer.backendtfg.infrastructure.controller.dto.input;

import lombok.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileInputDto {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    @NotBlank(message = "El apellido es obligatorio")
    private String surname;
    @NotBlank(message = "El DNI es obligatorio")
    private String dni;
} 