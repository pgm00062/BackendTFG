package freelancer.backendtfg.application.port;

import freelancer.backendtfg.infrastructure.controller.dto.input.ProjectCreateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.ProjectOutputDto;

public interface UpdateProjectUseCase {
    ProjectOutputDto updateProject(Long projectId, String userEmail, ProjectCreateInputDto inputDto);
} 