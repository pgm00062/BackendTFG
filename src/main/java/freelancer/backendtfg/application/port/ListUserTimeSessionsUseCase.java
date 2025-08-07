package freelancer.backendtfg.application.port;

import freelancer.backendtfg.infrastructure.controller.dto.output.TimeSessionOutputDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListUserTimeSessionsUseCase {
    Page<TimeSessionOutputDto> listTimeSessions(String userEmail, Pageable pageable);
} 