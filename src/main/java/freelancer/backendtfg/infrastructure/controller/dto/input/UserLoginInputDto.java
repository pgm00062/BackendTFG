package freelancer.backendtfg.infrastructure.controller.dto.input;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginInputDto {
    private String email;
    private String password;
}
