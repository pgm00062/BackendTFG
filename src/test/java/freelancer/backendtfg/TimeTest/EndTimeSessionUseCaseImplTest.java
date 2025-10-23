package freelancer.backendtfg.TimeTest;

import freelancer.backendtfg.application.useCaseImpl.timeUseCaseImpl.EndTimeSessionUseCaseImpl;
import freelancer.backendtfg.domain.mapper.TimeMapper;
import freelancer.backendtfg.infrastructure.controller.dto.input.timesInput.TimeEndInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.TimeSessionException;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para EndTimeSessionUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear finalización de sesiones de tiempo
 * - Verificar que solo el propietario puede finalizar su sesión
 * - Validar que solo sesiones activas pueden finalizarse
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Finalizar Sesión de Tiempo")
class EndTimeSessionUseCaseImplTest {

    @Mock
    private TimeRepositoryPort timeRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private TimeMapper timeMapper;

    @InjectMocks
    private EndTimeSessionUseCaseImpl endTimeSessionUseCase;

    private String userEmail;
    private UserEntity userEntity;
    private TimeEntity timeEntity;
    private TimeEndInputDto inputDto;
    private TimeSessionOutputDto expectedOutputDto;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(userEmail);

        timeEntity = new TimeEntity();
        timeEntity.setId(1L);
        timeEntity.setUser(userEntity);
        timeEntity.setStartTime(LocalDateTime.now().minusHours(2));
        timeEntity.setActive(true);
        timeEntity.setDescription("Sesión de trabajo");

        inputDto = new TimeEndInputDto();
        inputDto.setTimeSessionId(1L);
        inputDto.setDescription("Trabajo completado");

        expectedOutputDto = new TimeSessionOutputDto();
        expectedOutputDto.setId(1L);
        expectedOutputDto.setActive(false);
    }

    @Test
    @DisplayName("Debería finalizar sesión correctamente cuando está activa")
    void deberiaFinalizarSesionCorrectamente() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(timeRepository.findById(anyLong())).thenReturn(Optional.of(timeEntity));
        when(timeRepository.save(any(TimeEntity.class))).thenReturn(timeEntity);
        when(timeMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        TimeSessionOutputDto result = endTimeSessionUseCase.endSession(userEmail, inputDto);

        // ASSERT
        assertNotNull(result);
        assertFalse(timeEntity.isActive());
        assertNotNull(timeEntity.getEndTime());
        
        verify(timeRepository, times(1)).save(timeEntity);
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class,
            () -> endTimeSessionUseCase.endSession(userEmail, inputDto));

        verify(timeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar TimeSessionException cuando la sesión no existe")
    void deberiaLanzarExcepcionCuandoSesionNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(timeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        TimeSessionException exception = assertThrows(TimeSessionException.class,
            () -> endTimeSessionUseCase.endSession(userEmail, inputDto));

        assertEquals("Sesión de tiempo no encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar TimeSessionException cuando la sesión no pertenece al usuario")
    void deberiaLanzarExcepcionCuandoSesionNoPertenece() {
        // ARRANGE
        UserEntity otroUsuario = new UserEntity();
        otroUsuario.setEmail("otro@test.com");
        timeEntity.setUser(otroUsuario);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(timeRepository.findById(anyLong())).thenReturn(Optional.of(timeEntity));

        // ACT & ASSERT
        TimeSessionException exception = assertThrows(TimeSessionException.class,
            () -> endTimeSessionUseCase.endSession(userEmail, inputDto));

        assertEquals("No tienes permisos para finalizar esta sesión", exception.getMessage());
        verify(timeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar TimeSessionException cuando la sesión ya está finalizada")
    void deberiaLanzarExcepcionCuandoSesionYaFinalizada() {
        // ARRANGE
        timeEntity.setActive(false);
        timeEntity.setEndTime(LocalDateTime.now());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(timeRepository.findById(anyLong())).thenReturn(Optional.of(timeEntity));

        // ACT & ASSERT
        TimeSessionException exception = assertThrows(TimeSessionException.class,
            () -> endTimeSessionUseCase.endSession(userEmail, inputDto));

        assertEquals("La sesión ya está finalizada", exception.getMessage());
    }

    @Test
    @DisplayName("Debería actualizar descripción si se proporciona")
    void deberiaActualizarDescripcionSiSeProvee() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(timeRepository.findById(anyLong())).thenReturn(Optional.of(timeEntity));
        when(timeRepository.save(any())).thenReturn(timeEntity);
        when(timeMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        endTimeSessionUseCase.endSession(userEmail, inputDto);

        // ASSERT
        assertEquals("Trabajo completado", timeEntity.getDescription());
    }

    @Test
    @DisplayName("No debería actualizar descripción si es null o vacía")
    void noDeberiaActualizarDescripcionSiEsVacia() {
        // ARRANGE
        String descripcionOriginal = "Sesión de trabajo";
        inputDto.setDescription(null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(timeRepository.findById(anyLong())).thenReturn(Optional.of(timeEntity));
        when(timeRepository.save(any())).thenReturn(timeEntity);
        when(timeMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        endTimeSessionUseCase.endSession(userEmail, inputDto);

        // ASSERT
        assertEquals(descripcionOriginal, timeEntity.getDescription());
    }

    @Test
    @DisplayName("Debería establecer endTime con la hora actual")
    void deberiaEstablecerEndTimeConHoraActual() {
        // ARRANGE
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(timeRepository.findById(anyLong())).thenReturn(Optional.of(timeEntity));
        when(timeRepository.save(any())).thenAnswer(invocation -> {
            TimeEntity saved = invocation.getArgument(0);
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);
            
            assertNotNull(saved.getEndTime());
            assertTrue(saved.getEndTime().isAfter(before));
            assertTrue(saved.getEndTime().isBefore(after));
            
            return saved;
        });
        when(timeMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        endTimeSessionUseCase.endSession(userEmail, inputDto);

        // ASSERT
        verify(timeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Debería marcar sesión como inactiva al finalizar")
    void deberiaMarcasSesionComoInactiva() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(timeRepository.findById(anyLong())).thenReturn(Optional.of(timeEntity));
        when(timeRepository.save(any())).thenReturn(timeEntity);
        when(timeMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        assertTrue(timeEntity.isActive(), "Sesión debe estar activa antes");

        // ACT
        endTimeSessionUseCase.endSession(userEmail, inputDto);

        // ASSERT
        assertFalse(timeEntity.isActive(), "Sesión debe estar inactiva después");
    }
}
