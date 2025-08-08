package freelancer.backendtfg.application.port.timeUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectTotalTimeOutputDto;

public interface GetProjectTotalTimeUseCase {
    ProjectTotalTimeOutputDto getProjectTotalTime(String userEmail, Long projectId);
} 