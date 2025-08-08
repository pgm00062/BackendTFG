package freelancer.backendtfg.application.port.timeUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.input.timesInput.TimeEndInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionOutputDto;

public interface EndTimeSessionUseCase {
    TimeSessionOutputDto endSession(String userEmail, TimeEndInputDto inputDto);
} 