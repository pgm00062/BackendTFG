package freelancer.backendtfg.infrastructure.controller.dto.output;

import freelancer.backendtfg.domain.enums.ProjectType;
import freelancer.backendtfg.domain.enums.ProjectStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectOutputDto {
    private Long id;
    private String name;
    private String description;
    private ProjectType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal budget;
    private ProjectStatus status;
} 