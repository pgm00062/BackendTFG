package freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsOutputDto {
    private BigDecimal totalEarned;  // Dinero ganado en período
    private BigDecimal pendingMoney;  // Dinero pendiente (placeholder)
    private Double averageTimePerProject;  // Tiempo medio (placeholder)
    private BigDecimal profitabilityRatio;  // Ratio dinero/tiempo (placeholder)
}
