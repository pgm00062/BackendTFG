package freelancer.backendtfg.infrastructure.controller.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeEndInputDto {
    @NotNull(message = "El ID de la sesión de tiempo es obligatorio")
    private Long timeSessionId;
    
    private String description; // Descripción opcional de lo realizado
} 