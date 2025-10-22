package freelancer.backendtfg.application.port.timeUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionDailyOutputDto;

public interface GetStatiticsTimeUseCase {  
    TimeSessionDailyOutputDto getTotalTimeLastMonth(String email);
    TimeSessionDailyOutputDto getTotalTimeThisYear(String email);
}
