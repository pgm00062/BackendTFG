package freelancer.backendtfg.TimeTest;

import freelancer.backendtfg.application.useCaseImpl.timeUseCaseImpl.StartTimeSessionUseCaseImpl;
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
 * Tests Unitarios para StartTimeSessionUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear inicio de sesiones de tiempo (time tracking)
 * - Verificar que solo puede haber una sesión activa por usuario
 * - Validar asociación con usuario y proyecto
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Iniciar Sesión de Tiempo")
class StartTimeSessionUseCaseImplTest {

    @Mock
    private TimeRepositoryPort timeRepository;

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private TimeMapper timeMapper;

    @InjectMocks
    private StartTimeSessionUseCaseImpl startTimeSessionUseCase;

    private String userEmail;
    private UserEntity userEntity;
    private ProjectEntity projectEntity;
    private TimeStartInputDto inputDto;
    private TimeEntity timeEntity;
    private TimeSessionOutputDto expectedOutputDto;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(userEmail);

        projectEntity = new ProjectEntity();
        projectEntity.setId(1L);
        projectEntity.setName("Proyecto Web");

        inputDto = new TimeStartInputDto();
        inputDto.setProjectId(1L);
        inputDto.setDescription("Trabajando en feature X");

        timeEntity = new TimeEntity();
        timeEntity.setId(1L);
        timeEntity.setUser(userEntity);
        timeEntity.setProject(projectEntity);
        timeEntity.setStartTime(LocalDateTime.now());
        timeEntity.setActive(true);
        timeEntity.setDescription("Trabajando en feature X");

        expectedOutputDto = new TimeSessionOutputDto();
        expectedOutputDto.setId(1L);
        expectedOutputDto.setActive(true);
    }

    @Test
    @DisplayName("Debería iniciar sesión correctamente cuando no hay sesión activa")
    void deberiaIniciarSesionCorrectamente() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(timeRepository.existsActiveSessionByUserEmail(anyString())).thenReturn(false);
        when(timeRepository.save(any(TimeEntity.class))).thenReturn(timeEntity);
        when(timeMapper.toOutputDto(any(TimeEntity.class))).thenReturn(expectedOutputDto);

        // ACT
        TimeSessionOutputDto result = startTimeSessionUseCase.startSession(userEmail, inputDto);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isActive());
        
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(projectRepository, times(1)).findById(1L);
        verify(timeRepository, times(1)).existsActiveSessionByUserEmail(userEmail);
        verify(timeRepository, times(1)).save(any(TimeEntity.class));
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class,
            () -> startTimeSessionUseCase.startSession(userEmail, inputDto));

        verify(projectRepository, never()).findById(anyLong());
        verify(timeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el proyecto no existe")
    void deberiaLanzarExcepcionCuandoProyectoNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class,
            () -> startTimeSessionUseCase.startSession(userEmail, inputDto));

        verify(timeRepository, never()).existsActiveSessionByUserEmail(anyString());
        verify(timeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar ActiveSessionExistsException cuando ya hay una sesión activa")
    void deberiaLanzarExcepcionCuandoYaHaySesionActiva() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(timeRepository.existsActiveSessionByUserEmail(anyString())).thenReturn(true);

        // ACT & ASSERT
        ActiveSessionExistsException exception = assertThrows(
            ActiveSessionExistsException.class,
            () -> startTimeSessionUseCase.startSession(userEmail, inputDto)
        );

        assertEquals("Ya existe una sesión activa para este usuario", exception.getMessage());
        verify(timeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería crear sesión con estado activo y endTime null")
    void deberiaCrearSesionConEstadoActivoYEndTimeNull() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(timeRepository.existsActiveSessionByUserEmail(anyString())).thenReturn(false);
        when(timeRepository.save(any(TimeEntity.class))).thenAnswer(invocation -> {
            TimeEntity savedEntity = invocation.getArgument(0);
            
            // Verificamos que la sesión se crea correctamente
            assertTrue(savedEntity.isActive(), "La sesión debe estar activa");
            assertNull(savedEntity.getEndTime(), "EndTime debe ser null al inicio");
            assertNotNull(savedEntity.getStartTime(), "StartTime debe estar establecido");
            
            return savedEntity;
        });
        when(timeMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        startTimeSessionUseCase.startSession(userEmail, inputDto);

        // ASSERT
        verify(timeRepository, times(1)).save(any(TimeEntity.class));
    }

    @Test
    @DisplayName("Debería asociar usuario y proyecto a la sesión")
    void deberiaAsociarUsuarioYProyecto() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(timeRepository.existsActiveSessionByUserEmail(anyString())).thenReturn(false);
        when(timeRepository.save(any(TimeEntity.class))).thenAnswer(invocation -> {
            TimeEntity savedEntity = invocation.getArgument(0);
            
            assertEquals(userEntity, savedEntity.getUser());
            assertEquals(projectEntity, savedEntity.getProject());
            
            return savedEntity;
        });
        when(timeMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        startTimeSessionUseCase.startSession(userEmail, inputDto);

        // ASSERT
        verify(timeRepository, times(1)).save(any(TimeEntity.class));
    }

    @Test
    @DisplayName("Debería guardar la descripción de la sesión")
    void deberiaGuardarDescripcionDeLaSesion() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(timeRepository.existsActiveSessionByUserEmail(anyString())).thenReturn(false);
        when(timeRepository.save(any(TimeEntity.class))).thenAnswer(invocation -> {
            TimeEntity savedEntity = invocation.getArgument(0);
            
            assertEquals("Trabajando en feature X", savedEntity.getDescription());
            
            return savedEntity;
        });
        when(timeMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        startTimeSessionUseCase.startSession(userEmail, inputDto);

        // ASSERT
        verify(timeRepository, times(1)).save(any(TimeEntity.class));
    }

    @Test
    @DisplayName("Debería establecer startTime con la hora actual")
    void deberiaEstablecerStartTimeConHoraActual() {
        // ARRANGE
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(timeRepository.existsActiveSessionByUserEmail(anyString())).thenReturn(false);
        when(timeRepository.save(any(TimeEntity.class))).thenAnswer(invocation -> {
            TimeEntity savedEntity = invocation.getArgument(0);
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);
            
            assertNotNull(savedEntity.getStartTime());
            assertTrue(savedEntity.getStartTime().isAfter(before));
            assertTrue(savedEntity.getStartTime().isBefore(after));
            
            return savedEntity;
        });
        when(timeMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        startTimeSessionUseCase.startSession(userEmail, inputDto);

        // ASSERT
        verify(timeRepository, times(1)).save(any(TimeEntity.class));
    }

    @Test
    @DisplayName("Debería usar el mapper para convertir entidad a DTO")
    void deberiaUsarMapperParaConvertir() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(timeRepository.existsActiveSessionByUserEmail(anyString())).thenReturn(false);
        when(timeRepository.save(any())).thenReturn(timeEntity);
        when(timeMapper.toOutputDto(timeEntity)).thenReturn(expectedOutputDto);

        // ACT
        TimeSessionOutputDto result = startTimeSessionUseCase.startSession(userEmail, inputDto);

        // ASSERT
        verify(timeMapper, times(1)).toOutputDto(timeEntity);
        assertSame(expectedOutputDto, result);
    }
}
