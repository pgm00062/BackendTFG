package freelancer.backendtfg.infrastructure.controller.dto.input;

import freelancer.backendtfg.domain.enums.ProjectStatus;
import lombok.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatusUpdateInputDto {
    @NotNull(message = "El estado es obligatorio")
    private ProjectStatus status;
} 