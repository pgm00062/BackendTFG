package freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl;

import freelancer.backendtfg.application.port.projectUseCasePort.ListUserProjectsUseCase;
import freelancer.backendtfg.domain.enums.ProjectStatus;
import freelancer.backendtfg.domain.mapper.ProjectMapper;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListUserProjectsUseCaseImpl implements ListUserProjectsUseCase {
    private final ProjectRepositoryPort projectRepository;
    private final UserRepositoryPort userRepository;
    private final ProjectMapper projectMapper;

    @Override
    public Page<ProjectOutputDto> listProjects(String userEmail, Pageable pageable) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        Page<ProjectOutputDto> page = projectRepository.findByUserId(user.getId(), pageable)
                .map(projectMapper::toOutputDto);
        return page;
    }

    @Override
    public Page<ProjectOutputDto> listProjectsByStatus(String email, ProjectStatus status, Pageable pageable) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        Page<ProjectOutputDto> page = projectRepository.findByUserIdAndStatus(user.getId(), status, pageable)
                .map(projectMapper::toOutputDto);
        return page;
    }
} 