package freelancer.backendtfg.application.useCaseImpl.timeUseCaseImpl;

import freelancer.backendtfg.application.port.timeUseCasePort.ListUserTimeSessionsUseCase;
import freelancer.backendtfg.domain.mapper.TimeMapper;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListUserTimeSessionsUseCaseImpl implements ListUserTimeSessionsUseCase {
    private final TimeRepositoryPort timeRepository;
    private final UserRepositoryPort userRepository;
    private final TimeMapper timeMapper;

    @Override
    public Page<TimeSessionOutputDto> listTimeSessions(String userEmail, Pageable pageable) {
        // Verificar que el usuario existe
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        
        // Obtener las sesiones de tiempo del usuario
        Page<TimeEntity> timeEntities = timeRepository.findByUserEmail(userEmail, pageable);
        
        // Convertir a DTOs de salida
        return timeEntities.map(timeMapper::toOutputDto);
    }
} 