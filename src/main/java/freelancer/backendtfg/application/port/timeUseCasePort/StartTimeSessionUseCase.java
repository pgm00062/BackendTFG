package freelancer.backendtfg.application.port.timeUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.input.timesInput.TimeStartInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionOutputDto;

public interface StartTimeSessionUseCase {
    TimeSessionOutputDto startSession(String userEmail, TimeStartInputDto inputDto);
} 