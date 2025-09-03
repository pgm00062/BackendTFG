package freelancer.backendtfg.application.useCaseImpl.timeUseCaseImpl;

import java.util.jar.Pack200.Packer;

import org.springframework.stereotype.Service;

import freelancer.backendtfg.application.port.timeUseCasePort.PauseResumeTimeSessionUseCase;
import freelancer.backendtfg.domain.mapper.TimeMapper;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PauseResumeTimeSessionUseCaseImpl implements PauseResumeTimeSessionUseCase {
    
    private final TimeRepositoryPort timeRepository;
    private final TimeMapper timeMapper;

    @Override
    public TimeSessionOutputDto pauseSession(String userEmail) {
        TimeEntity activeSession = timeRepository.findActiveSessionByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("No hay sesión activa"));
        
        if (activeSession.isPaused()) {
            throw new IllegalArgumentException("La sesión ya está pausada");
        }
        
        TimeEntity paused = timeRepository.pauseSession(activeSession.getId())
                .orElseThrow(() -> new IllegalStateException("Error al pausar sesión"));
        
        return timeMapper.toOutputDto(paused);
    }

    @Override
    public TimeSessionOutputDto resumeSession(String userEmail) {
        TimeEntity activeSession = timeRepository.findActiveSessionByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("No hay sesión activa"));
        
        if (!activeSession.isPaused()) {
            throw new IllegalArgumentException("La sesión no está pausada");
        }
        
        TimeEntity resumed = timeRepository.resumeSession(activeSession.getId())
                .orElseThrow(() -> new IllegalStateException("Error al reanudar sesión"));
        
        return timeMapper.toOutputDto(resumed);
    }

}
