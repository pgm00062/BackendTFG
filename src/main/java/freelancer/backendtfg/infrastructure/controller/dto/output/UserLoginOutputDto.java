package freelancer.backendtfg.infrastructure.controller.dto.output;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginOutputDto {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String token;

    // Constructor sin token para /users/me
    public UserLoginOutputDto(Long id, String name, String surname, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
}
