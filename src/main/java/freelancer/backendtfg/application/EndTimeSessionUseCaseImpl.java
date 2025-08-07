package freelancer.backendtfg.application;

import freelancer.backendtfg.application.port.EndTimeSessionUseCase;
import freelancer.backendtfg.domain.mapper.TimeMapper;
import freelancer.backendtfg.infrastructure.controller.dto.input.TimeEndInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.TimeSessionOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.TimeSessionException;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EndTimeSessionUseCaseImpl implements EndTimeSessionUseCase {
    private final TimeRepositoryPort timeRepository;
    private final UserRepositoryPort userRepository;
    private final TimeMapper timeMapper;

    @Override
    public TimeSessionOutputDto endSession(String userEmail, TimeEndInputDto inputDto) {
        // Verificar que el usuario existe
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        
        // Buscar la sesión de tiempo
        TimeEntity timeEntity = timeRepository.findById(inputDto.getTimeSessionId())
                .orElseThrow(() -> new TimeSessionException("Sesión de tiempo no encontrada"));
        
        // Verificar que la sesión pertenece al usuario
        if (!timeEntity.getUser().getEmail().equals(userEmail)) {
            throw new TimeSessionException("No tienes permisos para finalizar esta sesión");
        }
        
        // Verificar que la sesión está activa
        if (!timeEntity.isActive()) {
            throw new TimeSessionException("La sesión ya está finalizada");
        }
        
        // Finalizar la sesión
        timeEntity.setEndTime(LocalDateTime.now());
        timeEntity.setActive(false);
        
        // Actualizar descripción si se proporciona
        if (inputDto.getDescription() != null && !inputDto.getDescription().trim().isEmpty()) {
            timeEntity.setDescription(inputDto.getDescription());
        }
        
        // Guardar la sesión actualizada
        TimeEntity savedTime = timeRepository.save(timeEntity);
        
        // Convertir a DTO de salida
        return timeMapper.toOutputDto(savedTime);
    }
} 