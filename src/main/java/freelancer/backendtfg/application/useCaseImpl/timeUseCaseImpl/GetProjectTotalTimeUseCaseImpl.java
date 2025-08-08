package freelancer.backendtfg.application.useCaseImpl.timeUseCaseImpl;

import freelancer.backendtfg.application.port.timeUseCasePort.GetProjectTotalTimeUseCase;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectTotalTimeOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetProjectTotalTimeUseCaseImpl implements GetProjectTotalTimeUseCase {
    private final TimeRepositoryPort timeRepository;
    private final ProjectRepositoryPort projectRepository;
    private final UserRepositoryPort userRepository;

    @Override
    public ProjectTotalTimeOutputDto getProjectTotalTime(String userEmail, Long projectId) {
        // Verificar que el usuario existe
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        
        // Verificar que el proyecto existe
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new UserNotFoundException("Proyecto no encontrado"));
        
        // Obtener todas las sesiones finalizadas del proyecto para este usuario
        List<TimeEntity> completedSessions = timeRepository.findCompletedSessionsByProjectIdAndUserEmail(projectId, userEmail);
        
        // Calcular tiempo total
        Duration totalDuration = Duration.ZERO;
        long totalMinutes = 0;
        
        for (TimeEntity session : completedSessions) {
            if (session.getEndTime() != null && session.getStartTime() != null) {
                Duration sessionDuration = Duration.between(session.getStartTime(), session.getEndTime());
                totalDuration = totalDuration.plus(sessionDuration);
                totalMinutes += sessionDuration.toMinutes();
            }
        }
        
        // Calcular estadísticas
        int totalSessions = completedSessions.size();
        double averageSessionTime = totalSessions > 0 ? (double) totalMinutes / totalSessions : 0.0;
        
        // Obtener fecha de la última sesión
        String lastSessionDate = completedSessions.isEmpty() ? null : 
            completedSessions.get(0).getStartTime().toString();
        
        return ProjectTotalTimeOutputDto.builder()
                .projectId(projectId)
                .projectName(project.getName())
                .userId(user.getId())
                .userName(user.getName() + " " + user.getSurname())
                .totalDuration(totalDuration)
                .totalMinutes(totalMinutes)
                .totalHours(totalMinutes / 60.0)
                .formattedTotalTime(formatDuration(totalDuration))
                .totalSessions(totalSessions)
                .averageSessionTime(averageSessionTime)
                .lastSessionDate(lastSessionDate)
                .build();
    }
    
    private String formatDuration(Duration duration) {
        if (duration == null) {
            return "0m";
        }
        
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }
} 