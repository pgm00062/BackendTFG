package freelancer.backendtfg.application.useCaseImpl.timeUseCaseImpl;

import freelancer.backendtfg.application.port.timeUseCasePort.GetActiveTimeSessionUseCase;
import freelancer.backendtfg.domain.mapper.TimeMapper;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetActiveTimeSessionUseCaseImpl implements GetActiveTimeSessionUseCase {
    private final TimeRepositoryPort timeRepository;
    private final UserRepositoryPort userRepository;
    private final TimeMapper timeMapper;

    @Override
    public TimeSessionOutputDto getActiveSession(String userEmail) {
        // Verificar que el usuario existe
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        
        // Buscar la sesión activa
        TimeEntity activeSession = timeRepository.findActiveSessionByUserEmail(userEmail)
                .orElse(null);
        
        // Si no hay sesión activa, devolver null o crear un DTO vacío
        if (activeSession == null) {
            return null;
        }
        
        // Convertir a DTO de salida
        return timeMapper.toOutputDto(activeSession);
    }
} 