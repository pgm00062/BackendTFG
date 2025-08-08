package freelancer.backendtfg.infrastructure.controller.dto.input.projectsInput;

import freelancer.backendtfg.domain.enums.ProjectType;
import freelancer.backendtfg.domain.enums.ProjectStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO de entrada para crear o actualizar un proyecto")
public class ProjectCreateInputDto {

    @ApiModelProperty(value = "Nombre del proyecto", required = true, example = "Desarrollo de App móvil")
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @ApiModelProperty(value = "Descripción del proyecto", required = true, example = "Proyecto para crear una aplicación móvil multiplataforma")
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    @ApiModelProperty(value = "Tipo de proyecto", required = true, example = "DESARROLLO")
    @NotNull(message = "El tipo es obligatorio")
    private ProjectType type;

    @ApiModelProperty(value = "Fecha de inicio del proyecto", required = true, example = "2025-08-01")
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    @ApiModelProperty(value = "Fecha de fin del proyecto", required = true, example = "2025-12-31")
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate endDate;

    @ApiModelProperty(value = "Presupuesto del proyecto", required = true, example = "10000.00")
    @NotNull(message = "El presupuesto es obligatorio")
    private BigDecimal budget;

    @ApiModelProperty(value = "Estado del proyecto", example = "EN_PROGRESO")
    private ProjectStatus status = ProjectStatus.EN_PROGRESO;
}