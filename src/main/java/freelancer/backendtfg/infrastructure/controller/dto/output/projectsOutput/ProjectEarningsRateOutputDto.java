package freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO de salida para la tasa de ganancia de un proyecto individual")
public class ProjectEarningsRateOutputDto {

    @ApiModelProperty(value = "ID del proyecto", example = "1")
    private Long projectId;

    @ApiModelProperty(value = "Nombre del proyecto", example = "Proyecto Alpha")
    private String projectName;

    @ApiModelProperty(value = "Presupuesto total del proyecto", example = "5000.00")
    private BigDecimal budget;

    @ApiModelProperty(value = "Total de horas trabajadas en el proyecto", example = "100.5")
    private double totalHours;

    @ApiModelProperty(value = "Tasa de ganancia por hora (€/hora)", example = "49.75")
    private BigDecimal earningsPerHour;

    @ApiModelProperty(value = "Estado del proyecto", example = "TERMINADO")
    private String status;
}
