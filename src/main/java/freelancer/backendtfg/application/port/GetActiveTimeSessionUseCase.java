package freelancer.backendtfg.application.port;

import freelancer.backendtfg.infrastructure.controller.dto.output.TimeSessionOutputDto;

public interface GetActiveTimeSessionUseCase {
    TimeSessionOutputDto getActiveSession(String userEmail);
} 