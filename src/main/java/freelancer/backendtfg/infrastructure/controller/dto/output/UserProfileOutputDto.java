package freelancer.backendtfg.infrastructure.controller.dto.output;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileOutputDto {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String dni;
} 