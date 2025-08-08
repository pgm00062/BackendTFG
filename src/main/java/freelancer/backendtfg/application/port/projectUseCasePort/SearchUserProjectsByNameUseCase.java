package freelancer.backendtfg.application.port.projectUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchUserProjectsByNameUseCase {
    Page<ProjectOutputDto> searchProjects(String userEmail, String name, Pageable pageable);
} 