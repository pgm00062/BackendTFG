package freelancer.backendtfg.application.port.projectUseCasePort;

import java.math.BigDecimal;

public interface GetStatiticsUseCase {
    BigDecimal getEarningsLastMonth(String email);
    BigDecimal getPendingEarnings(String email);
    BigDecimal getEarningsThisYear(String email);
}
