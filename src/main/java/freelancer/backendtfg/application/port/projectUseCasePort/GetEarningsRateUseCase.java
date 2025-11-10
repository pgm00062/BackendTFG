package freelancer.backendtfg.application.port.projectUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.EarningsRateSummaryOutputDto;

public interface GetEarningsRateUseCase {
    EarningsRateSummaryOutputDto getEarningsRate(String email);
}
