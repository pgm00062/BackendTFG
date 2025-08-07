package freelancer.backendtfg.application.port;

import freelancer.backendtfg.infrastructure.controller.dto.input.TimeStartInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.TimeSessionOutputDto;

public interface StartTimeSessionUseCase {
    TimeSessionOutputDto startSession(String userEmail, TimeStartInputDto inputDto);
} 