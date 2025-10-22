package freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl;

import freelancer.backendtfg.application.port.projectUseCasePort.GetStatiticsUseCase;
import freelancer.backendtfg.domain.enums.ProjectStatus;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetStatiticsUseCaseImpl implements GetStatiticsUseCase {

    private final ProjectRepositoryPort projectRepositoryPort;

    @Override
    public BigDecimal getEarningsLastMonth(){
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        return projectRepositoryPort.getTotalBudgetByStatus(ProjectStatus.TERMINADO, oneMonthAgo);
    }
    @Override
    public BigDecimal getPendingEarnings(){
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        return projectRepositoryPort.getTotalBudgetByStatus(ProjectStatus.EN_PROGRESO,oneMonthAgo);
    }
    @Override
    public BigDecimal getEarningsThisYear() {
        int currentYear = LocalDate.now().getYear();
        return projectRepositoryPort.getTotalBudgetByStatusAndYear(ProjectStatus.TERMINADO, currentYear);
    }
}
