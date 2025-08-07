package freelancer.backendtfg.application.port;

import freelancer.backendtfg.infrastructure.controller.dto.output.ProjectTotalTimeOutputDto;

public interface GetProjectTotalTimeUseCase {
    ProjectTotalTimeOutputDto getProjectTotalTime(String userEmail, Long projectId);
} 