package freelancer.backendtfg.TimeTest;

import freelancer.backendtfg.application.useCaseImpl.timeUseCaseImpl.GetActiveTimeSessionUseCaseImpl;
import freelancer.backendtfg.domain.mapper.TimeMapper;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para GetActiveTimeSessionUseCaseImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Obtener Sesión Activa")
class GetActiveTimeSessionUseCaseImplTest {

    @Mock
    private TimeRepositoryPort timeRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private TimeMapper timeMapper;

    @InjectMocks
    private GetActiveTimeSessionUseCaseImpl getActiveTimeSessionUseCase;

    private String userEmail;
    private UserEntity userEntity;
    private TimeEntity activeSession;
    private TimeSessionOutputDto expectedOutputDto;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(userEmail);

        activeSession = new TimeEntity();
        activeSession.setId(1L);
        activeSession.setActive(true);
        activeSession.setStartTime(LocalDateTime.now());

        expectedOutputDto = new TimeSessionOutputDto();
        expectedOutputDto.setId(1L);
    }

    @Test
    @DisplayName("Debería obtener sesión activa cuando existe")
    void deberiaObtenerSesionActivaCuandoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(timeRepository.findActiveSessionByUserEmail(anyString())).thenReturn(Optional.of(activeSession));
        when(timeMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        TimeSessionOutputDto result = getActiveTimeSessionUseCase.getActiveSession(userEmail);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(timeRepository, times(1)).findActiveSessionByUserEmail(userEmail);
    }

    @Test
    @DisplayName("Debería retornar null cuando no hay sesión activa")
    void deberiaRetornarNullCuandoNoHaySesionActiva() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(timeRepository.findActiveSessionByUserEmail(anyString())).thenReturn(Optional.empty());

        // ACT
        TimeSessionOutputDto result = getActiveTimeSessionUseCase.getActiveSession(userEmail);

        // ASSERT
        assertNull(result);
        verify(timeMapper, never()).toOutputDto(any());
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class,
            () -> getActiveTimeSessionUseCase.getActiveSession(userEmail));

        verify(timeRepository, never()).findActiveSessionByUserEmail(anyString());
    }
}
