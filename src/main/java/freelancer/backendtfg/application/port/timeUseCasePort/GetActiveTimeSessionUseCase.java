package freelancer.backendtfg.application.port.timeUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionOutputDto;

public interface GetActiveTimeSessionUseCase {
    TimeSessionOutputDto getActiveSession(String userEmail);
} 