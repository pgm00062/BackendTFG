package freelancer.backendtfg.application.port.projectUseCasePort;

import java.math.BigDecimal;

public interface GetStatiticsUseCase {
    BigDecimal getEarningsLastMonth();
    BigDecimal getPendingEarnings();
    BigDecimal getEarningsThisYear();
}
