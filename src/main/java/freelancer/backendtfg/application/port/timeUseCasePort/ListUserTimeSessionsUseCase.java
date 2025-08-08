package freelancer.backendtfg.application.port.timeUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionOutputDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListUserTimeSessionsUseCase {
    Page<TimeSessionOutputDto> listTimeSessions(String userEmail, Pageable pageable);
} 