package freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "DTO de salida para la información de usuario y token de login")
public class UserLoginOutputDto {

    @ApiModelProperty(value = "ID único del usuario", example = "7")
    private Long id;

    @ApiModelProperty(value = "Nombre del usuario", example = "Pablo")
    private String name;

    @ApiModelProperty(value = "Apellido del usuario", example = "García")
    private String surname;

    @ApiModelProperty(value = "Email del usuario", example = "pablo@example.com")
    private String email;

    @ApiModelProperty(value = "Token JWT para autenticación", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    // Constructor sin token para /users/me
    public UserLoginOutputDto(Long id, String name, String surname, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
}