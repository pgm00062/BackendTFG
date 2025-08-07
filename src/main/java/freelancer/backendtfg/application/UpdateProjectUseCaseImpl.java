package freelancer.backendtfg.application;

import freelancer.backendtfg.application.port.UpdateProjectUseCase;
import freelancer.backendtfg.domain.mapper.ProjectMapper;
import freelancer.backendtfg.infrastructure.controller.dto.input.ProjectCreateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.ProjectOutputDto;
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
public class UpdateProjectUseCaseImpl implements UpdateProjectUseCase {
    private final ProjectRepositoryPort projectRepository;
    private final UserRepositoryPort userRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectOutputDto updateProject(Long projectId, String userEmail, ProjectCreateInputDto inputDto) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        if (!Objects.equals(project.getUser().getId(), user.getId())) {
            throw new RuntimeException("No tienes permiso para modificar este proyecto");
        }
        project.setName(inputDto.getName());
        project.setDescription(inputDto.getDescription());
        project.setType(inputDto.getType());
        project.setStartDate(inputDto.getStartDate());
        project.setEndDate(inputDto.getEndDate());
        project.setBudget(inputDto.getBudget());
        ProjectEntity updated = projectRepository.save(project);
        return projectMapper.toOutputDto(updated);
    }
} 