package freelancer.backendtfg.application.useCaseImpl.timeUseCaseImpl;


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
        
        // Usar el método de la entidad para pausar
        activeSession.pauseSession();
        
        // Guardar la entidad modificada
        TimeEntity paused = timeRepository.save(activeSession);
        
        return timeMapper.toOutputDto(paused);
    }

    @Override
    public TimeSessionOutputDto resumeSession(String userEmail) {
        TimeEntity activeSession = timeRepository.findActiveSessionByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("No hay sesión activa"));
        
        if (!activeSession.isPaused()) {
            throw new IllegalArgumentException("La sesión no está pausada");
        }
        
        // Usar el método de la entidad para reanudar
        activeSession.resumeSession();
        
        // Guardar la entidad modificada
        TimeEntity resumed = timeRepository.save(activeSession);
        
        return timeMapper.toOutputDto(resumed);
    }

}
