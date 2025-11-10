package freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl;

import freelancer.backendtfg.application.port.projectUseCasePort.GetStatiticsUseCase;
import freelancer.backendtfg.domain.enums.ProjectStatus;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetStatiticsUseCaseImpl implements GetStatiticsUseCase {

    private final ProjectRepositoryPort projectRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public BigDecimal getEarningsLastMonth(String email) {
        UserEntity user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        return projectRepositoryPort.getTotalBudgetByStatusAndUser(
                ProjectStatus.TERMINADO, 
                user.getId(), 
                oneMonthAgo
        );
    }

    @Override
    public BigDecimal getPendingEarnings(String email) {
        UserEntity user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        
        // Ganancias pendientes = proyectos EN_PROGRESO (sin filtro de fecha)
        return projectRepositoryPort.getTotalBudgetByStatusAndUser(
                ProjectStatus.EN_PROGRESO, 
                user.getId(), 
                null
        );
    }

    @Override
    public BigDecimal getEarningsThisYear(String email) {
        UserEntity user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        
        int currentYear = LocalDate.now().getYear();
        return projectRepositoryPort.getTotalBudgetByStatusUserAndYear(
                ProjectStatus.TERMINADO, 
                user.getId(), 
                currentYear
        );
    }
}
