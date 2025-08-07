package freelancer.backendtfg.application.port;

import freelancer.backendtfg.infrastructure.controller.dto.input.ProjectCreateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.ProjectOutputDto;

public interface CreateProjectUseCase {
    ProjectOutputDto createProject(String userEmail, ProjectCreateInputDto inputDto);
} 