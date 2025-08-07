package freelancer.backendtfg.infrastructure.controller.dto.input;

import lombok.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeStartInputDto {
    @NotNull(message = "El id del proyecto es obligatorio")
    private Long projectId;
    
    private String description; // Descripción opcional de la tarea a realizar
} 