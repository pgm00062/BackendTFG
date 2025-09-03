package freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl;

import freelancer.backendtfg.application.port.projectUseCasePort.GetProjectByIdUseCase;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDtoParcial;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.domain.mapper.ProjectMapper;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetProjectByIdUseCaseImpl implements GetProjectByIdUseCase {

    private final ProjectRepositoryPort projectRepository;
    private final UserRepositoryPort userRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectOutputDto getProjectById(Long projectId, String userEmail) {
        // Validar que el usuario existe
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // Validar que el proyecto existe
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // Validar que el usuario es el propietario
        if (!project.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes acceso a este proyecto");
        }

        // Devolver DTO de salida
        return projectMapper.toOutputDto(project);
    }

}
