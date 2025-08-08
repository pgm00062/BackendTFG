package freelancer.backendtfg.application.useCaseImpl.timeUseCaseImpl;

import freelancer.backendtfg.application.port.timeUseCasePort.StartTimeSessionUseCase;
import freelancer.backendtfg.domain.mapper.TimeMapper;
import freelancer.backendtfg.infrastructure.controller.dto.input.timesInput.TimeStartInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.ActiveSessionExistsException;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StartTimeSessionUseCaseImpl implements StartTimeSessionUseCase {
    private final TimeRepositoryPort timeRepository;
    private final ProjectRepositoryPort projectRepository;
    private final UserRepositoryPort userRepository;
    private final TimeMapper timeMapper;

    @Override
    public TimeSessionOutputDto startSession(String userEmail, TimeStartInputDto inputDto) {
        // Verificar que el usuario existe
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        
        // Verificar que el proyecto existe
        ProjectEntity project = projectRepository.findById(inputDto.getProjectId())
                .orElseThrow(() -> new UserNotFoundException("Proyecto no encontrado"));
        
        // Verificar que no hay una sesión activa para este usuario
        if (timeRepository.existsActiveSessionByUserEmail(userEmail)) {
            throw new ActiveSessionExistsException("Ya existe una sesión activa para este usuario");
        }
        
        // Crear nueva sesión de tiempo
        TimeEntity timeEntity = new TimeEntity();
        timeEntity.setUser(user);
        timeEntity.setProject(project);
        timeEntity.setStartTime(LocalDateTime.now());
        timeEntity.setEndTime(null);
        timeEntity.setActive(true);
        timeEntity.setDescription(inputDto.getDescription());
        
        // Guardar la sesión
        TimeEntity savedTime = timeRepository.save(timeEntity);
        
        // Convertir a DTO de salida
        return timeMapper.toOutputDto(savedTime);
    }
} 