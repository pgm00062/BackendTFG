package freelancer.backendtfg.application.port.projectUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDtoParcial;

public interface GetProjectByIdUseCase {
    ProjectOutputDto getProjectById(Long projectId, String userEmail);
}
