package freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSessionDailyOutputDto {
    private double totalHours;
    private long totalMinutes;
}
