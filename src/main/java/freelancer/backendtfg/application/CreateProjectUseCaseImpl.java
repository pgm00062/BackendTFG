package freelancer.backendtfg.application;

import freelancer.backendtfg.application.port.CreateProjectUseCase;
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

@Service
@RequiredArgsConstructor
public class CreateProjectUseCaseImpl implements CreateProjectUseCase {
    private final ProjectRepositoryPort projectRepository;
    private final UserRepositoryPort userRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectOutputDto createProject(String userEmail, ProjectCreateInputDto inputDto) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        ProjectEntity project = projectMapper.toEntity(inputDto);
        project.setUser(user);
        ProjectEntity saved = projectRepository.save(project);
        return projectMapper.toOutputDto(saved);
    }
} 