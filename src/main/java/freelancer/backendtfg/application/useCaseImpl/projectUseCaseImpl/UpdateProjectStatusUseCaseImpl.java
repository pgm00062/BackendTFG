package freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl;

import freelancer.backendtfg.application.port.projectUseCasePort.UpdateProjectStatusUseCase;
import freelancer.backendtfg.domain.mapper.ProjectMapper;
import freelancer.backendtfg.infrastructure.controller.dto.input.projectsInput.ProjectStatusUpdateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UpdateProjectStatusUseCaseImpl implements UpdateProjectStatusUseCase {
    private final ProjectRepositoryPort projectRepository;
    private final UserRepositoryPort userRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectOutputDto updateStatus(Long projectId, String userEmail, ProjectStatusUpdateInputDto inputDto) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        if (!Objects.equals(project.getUser().getId(), user.getId())) {
            throw new RuntimeException("No tienes permiso para modificar este proyecto");
        }
        project.setStatus(inputDto.getStatus());
        ProjectEntity updated = projectRepository.save(project);
        return projectMapper.toOutputDto(updated);
    }
} 