package freelancer.backendtfg.infrastructure.controller.dto.input;

import freelancer.backendtfg.domain.enums.ProjectType;
import freelancer.backendtfg.domain.enums.ProjectStatus;
import lombok.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateInputDto {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    @NotNull(message = "El tipo es obligatorio")
    private ProjectType type;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate endDate;

    @NotNull(message = "El presupuesto es obligatorio")
    private BigDecimal budget;

    private ProjectStatus status = ProjectStatus.EN_PROGRESO;
} 