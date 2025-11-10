package freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO de salida para el resumen de tasas de ganancia")
public class EarningsRateSummaryOutputDto {

    @ApiModelProperty(value = "Tasa media de ganancia por hora de todos los proyectos (€/hora)", example = "45.50")
    private BigDecimal averageEarningsPerHour;

    @ApiModelProperty(value = "Total de horas trabajadas en todos los proyectos", example = "250.5")
    private double totalHours;

    @ApiModelProperty(value = "Presupuesto total de todos los proyectos", example = "15000.00")
    private BigDecimal totalBudget;

    @ApiModelProperty(value = "Número de proyectos analizados", example = "5")
    private int totalProjects;

    @ApiModelProperty(value = "Lista de tasas de ganancia por proyecto")
    private List<ProjectEarningsRateOutputDto> projectRates;
}
