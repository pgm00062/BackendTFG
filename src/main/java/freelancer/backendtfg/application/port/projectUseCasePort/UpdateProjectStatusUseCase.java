package freelancer.backendtfg.application.port.projectUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.input.projectsInput.ProjectStatusUpdateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDto;

public interface UpdateProjectStatusUseCase {
    ProjectOutputDto updateStatus(Long projectId, String userEmail, ProjectStatusUpdateInputDto inputDto);
} 