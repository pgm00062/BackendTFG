package freelancer.backendtfg.application.port;

import freelancer.backendtfg.infrastructure.controller.dto.input.TimeEndInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.TimeSessionOutputDto;

public interface EndTimeSessionUseCase {
    TimeSessionOutputDto endSession(String userEmail, TimeEndInputDto inputDto);
} 