package freelancer.backendtfg.application.useCaseImpl.timeUseCaseImpl;

import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionDailyOutputDto;
import freelancer.backendtfg.application.port.timeUseCasePort.GetStatiticsTimeUseCase;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetStatiticsTimeUseCaseImpl implements GetStatiticsTimeUseCase {

    private final TimeRepositoryPort timeRepository;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public TimeSessionDailyOutputDto getTotalTimeLastMonth(String email) {
        // Validar que el usuario existe
        userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        
        // Calcular rango de fechas del último mes
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        LocalDateTime now = LocalDateTime.now();
        
        // Obtener sesiones completadas en el rango
        List<TimeEntity> sessions = timeRepository.findCompletedSessionsByUserEmailAndDateRange(
                email, oneMonthAgo, now);
        
        // Calcular totales
        long totalMinutes = sessions.stream()
                .mapToLong(TimeEntity::getDurationInMinutes)
                .sum();
        double totalHours = totalMinutes / 60.0;
        
        return new TimeSessionDailyOutputDto(totalHours, totalMinutes);
    }

    @Override
    public TimeSessionDailyOutputDto getTotalTimeThisYear(String email) {
        // Validar que el usuario existe
        userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        
        // Obtener año actual
        int currentYear = LocalDateTime.now().getYear();
        
        // Obtener sesiones completadas del año
        List<TimeEntity> sessions = timeRepository.findCompletedSessionsByUserEmailAndYear(
                email, currentYear);
        
        // Calcular totales
        long totalMinutes = sessions.stream()
                .mapToLong(TimeEntity::getDurationInMinutes)
                .sum();
        double totalHours = totalMinutes / 60.0;
        
        return new TimeSessionDailyOutputDto(totalHours, totalMinutes);
    }
}
