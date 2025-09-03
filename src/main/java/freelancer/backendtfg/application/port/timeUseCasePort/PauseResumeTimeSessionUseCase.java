package freelancer.backendtfg.application.port.timeUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionOutputDto;


public interface PauseResumeTimeSessionUseCase {
    TimeSessionOutputDto pauseSession(String userEmail);
    TimeSessionOutputDto resumeSession(String userEmail);
}
