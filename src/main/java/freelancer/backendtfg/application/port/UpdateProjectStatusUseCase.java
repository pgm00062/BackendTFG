package freelancer.backendtfg.application.port;

import freelancer.backendtfg.infrastructure.controller.dto.input.ProjectStatusUpdateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.ProjectOutputDto;

public interface UpdateProjectStatusUseCase {
    ProjectOutputDto updateStatus(Long projectId, String userEmail, ProjectStatusUpdateInputDto inputDto);
} 