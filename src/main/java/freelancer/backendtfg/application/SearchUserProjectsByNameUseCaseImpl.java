package freelancer.backendtfg.application;

import freelancer.backendtfg.application.port.SearchUserProjectsByNameUseCase;
import freelancer.backendtfg.domain.mapper.ProjectMapper;
import freelancer.backendtfg.infrastructure.controller.dto.output.ProjectOutputDto;
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
public class SearchUserProjectsByNameUseCaseImpl implements SearchUserProjectsByNameUseCase {
    private final ProjectRepositoryPort projectRepository;
    private final UserRepositoryPort userRepository;
    private final ProjectMapper projectMapper;

    @Override
    public Page<ProjectOutputDto> searchProjects(String userEmail, String name, Pageable pageable) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        return projectRepository.findByUserIdAndNameContainingIgnoreCase(user.getId(), name, pageable)
                .map(projectMapper::toOutputDto);
    }
} 