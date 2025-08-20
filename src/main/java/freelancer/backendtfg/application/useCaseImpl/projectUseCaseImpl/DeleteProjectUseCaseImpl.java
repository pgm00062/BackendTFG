package freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl;

import freelancer.backendtfg.application.port.projectUseCasePort.DeleteProjectUseCase;
import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteProjectUseCaseImpl implements DeleteProjectUseCase {
    private final ProjectRepositoryPort projectRepository;
    private final UserRepositoryPort userRepository;
    private final TimeRepositoryPort timeRepository;

    @Override
    @Transactional
    public void deleteProject(Long projectId, String userEmail) {
        log.info("Iniciando eliminación del proyecto con ID: {} por usuario: {}", projectId, userEmail);

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (!Objects.equals(project.getUser().getId(), user.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar este proyecto");
        }

        // Eliminar todos los tiempos asociados primero
        log.info("Eliminando tiempos asociados al proyecto: {}", projectId);
        timeRepository.deleteByProjectId(projectId);

        // Eliminar el proyecto
        log.info("Eliminando proyecto: {}", projectId);
        projectRepository.delete(project);

        log.info("Proyecto eliminado exitosamente: {}", projectId);
    }
}