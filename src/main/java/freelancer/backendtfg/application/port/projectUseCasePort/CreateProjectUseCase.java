package freelancer.backendtfg.application.port.projectUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.input.projectsInput.ProjectCreateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDto;

public interface CreateProjectUseCase {
    ProjectOutputDto createProject(String userEmail, ProjectCreateInputDto inputDto);
} 