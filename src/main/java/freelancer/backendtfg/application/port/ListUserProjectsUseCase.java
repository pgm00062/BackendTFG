package freelancer.backendtfg.application.port;

import freelancer.backendtfg.infrastructure.controller.dto.output.ProjectOutputDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListUserProjectsUseCase {
    Page<ProjectOutputDto> listProjects(String userEmail, Pageable pageable);
} 