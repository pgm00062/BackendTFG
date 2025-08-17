package freelancer.backendtfg.application.port.projectUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDto;

public interface GetProjectByIdUseCase {
    ProjectOutputDto getProjectById(Long projectId, String userEmail);
}
