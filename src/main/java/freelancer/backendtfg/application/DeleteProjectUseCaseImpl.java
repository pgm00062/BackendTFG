package freelancer.backendtfg.application;

import freelancer.backendtfg.application.port.DeleteProjectUseCase;
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
public class DeleteProjectUseCaseImpl implements DeleteProjectUseCase {
    private final ProjectRepositoryPort projectRepository;
    private final UserRepositoryPort userRepository;

    @Override
    public void deleteProject(Long projectId, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        if (!Objects.equals(project.getUser().getId(), user.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar este proyecto");
        }
        projectRepository.delete(project);
    }
} 