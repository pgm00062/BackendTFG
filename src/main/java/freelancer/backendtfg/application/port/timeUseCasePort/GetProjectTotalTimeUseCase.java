package freelancer.backendtfg.application.port.timeUseCasePort;

import java.time.LocalDate;

import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectTotalTimeOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionDailyOutputDto;

public interface GetProjectTotalTimeUseCase {
    ProjectTotalTimeOutputDto getProjectTotalTime(String userEmail, Long projectId);
    TimeSessionDailyOutputDto getDailyTotalTime(String userEmail, LocalDate date);
} 