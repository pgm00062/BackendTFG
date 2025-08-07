package freelancer.backendtfg.infrastructure.controller.dto.output;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterOutputDto {
    private Long id;
    private String name;
    private String email;
}
