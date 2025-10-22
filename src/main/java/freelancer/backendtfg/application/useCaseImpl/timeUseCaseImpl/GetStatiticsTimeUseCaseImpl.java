package freelancer.backendtfg.application.useCaseImpl.timeUseCaseImpl;

import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionDailyOutputDto;
import freelancer.backendtfg.application.port.timeUseCasePort.GetStatiticsTimeUseCase;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetStatiticsTimeUseCaseImpl implements GetStatiticsTimeUseCase {

    private final TimeRepositoryPort timeRepository;

    @Override
    public TimeSessionDailyOutputDto getTotalTimeLastMonth(String email){
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime now = LocalDateTime.now();
        List<TimeEntity> sessions = timeRepository.findCompletedSessionsByUserEmailAndDateRange(email, oneMonthAgo, now);
        long totalMinutes = sessions.stream().mapToLong(TimeEntity::getDurationInMinutes).sum();
        double totalHours = totalMinutes / 60.0;
        return new TimeSessionDailyOutputDto(totalHours,totalMinutes);
    }

    @Override
    public TimeSessionDailyOutputDto getTotalTimeThisYear(String email){
        int currentYear = LocalDateTime.now().getYear();
        List<TimeEntity> sessions = timeRepository.findCompletedSessionsByUserEmailAndYear(email, currentYear);
        long totalMinutes = sessions.stream().mapToLong(TimeEntity::getDurationInMinutes).sum();
        double totalHours = totalMinutes / 60.0;
        return new TimeSessionDailyOutputDto(totalHours,totalMinutes);
    }
}
