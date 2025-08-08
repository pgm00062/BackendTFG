package freelancer.backendtfg.application.port.projectUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListUserProjectsUseCase {
    Page<ProjectOutputDto> listProjects(String userEmail, Pageable pageable);
} 