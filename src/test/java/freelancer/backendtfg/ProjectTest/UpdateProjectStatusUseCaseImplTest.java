package freelancer.backendtfg.ProjectTest;

import freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl.UpdateProjectStatusUseCaseImpl;
import freelancer.backendtfg.domain.enums.ProjectStatus;
import freelancer.backendtfg.domain.enums.ProjectType;
import freelancer.backendtfg.domain.mapper.ProjectMapper;
import freelancer.backendtfg.infrastructure.controller.dto.input.projectsInput.ProjectStatusUpdateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para UpdateProjectStatusUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear la actualización SOLO del estado de un proyecto
 * - Caso de uso específico para cambios de estado (EN_PROGRESO, TERMINADO, CANCELADO)
 * - Validar propiedad antes de permitir el cambio
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Actualizar Estado de Proyecto")
class UpdateProjectStatusUseCaseImplTest {

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private UpdateProjectStatusUseCaseImpl updateProjectStatusUseCase;

    private String userEmail;
    private Long projectId;
    private UserEntity userEntity;
    private ProjectEntity projectEntity;
    private ProjectStatusUpdateInputDto inputDto;
    private ProjectOutputDto expectedOutputDto;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";
        projectId = 1L;

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(userEmail);

        projectEntity = new ProjectEntity();
        projectEntity.setId(projectId);
        projectEntity.setName("Proyecto Test");
        projectEntity.setType(ProjectType.DESARROLLO);
        projectEntity.setStatus(ProjectStatus.EN_PROGRESO);
        projectEntity.setUser(userEntity);

        inputDto = new ProjectStatusUpdateInputDto();
        inputDto.setStatus(ProjectStatus.TERMINADO);

        expectedOutputDto = new ProjectOutputDto();
        expectedOutputDto.setId(projectId);
        expectedOutputDto.setStatus(ProjectStatus.TERMINADO);
    }

    @Test
    @DisplayName("Debería actualizar el estado correctamente cuando el usuario es propietario")
    void deberiaActualizarEstadoCorrectamente() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(projectRepository.save(any(ProjectEntity.class))).thenReturn(projectEntity);
        when(projectMapper.toOutputDto(any(ProjectEntity.class))).thenReturn(expectedOutputDto);

        // ACT
        ProjectOutputDto result = updateProjectStatusUseCase.updateStatus(projectId, userEmail, inputDto);

        // ASSERT
        assertNotNull(result);
        assertEquals(ProjectStatus.TERMINADO, projectEntity.getStatus());

        verify(projectRepository, times(1)).save(projectEntity);
        verify(projectMapper, times(1)).toOutputDto(projectEntity);
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class,
            () -> updateProjectStatusUseCase.updateStatus(projectId, userEmail, inputDto));

        verify(projectRepository, never()).findById(anyLong());
        verify(projectRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException cuando el proyecto no existe")
    void deberiaLanzarExcepcionCuandoProyectoNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> updateProjectStatusUseCase.updateStatus(projectId, userEmail, inputDto));

        assertEquals("Proyecto no encontrado", exception.getMessage());
        verify(projectRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException cuando el usuario no es propietario")
    void deberiaLanzarExcepcionCuandoUsuarioNoEsPropietario() {
        // ARRANGE
        UserEntity otroUsuario = new UserEntity();
        otroUsuario.setId(2L);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(otroUsuario));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> updateProjectStatusUseCase.updateStatus(projectId, userEmail, inputDto));

        assertEquals("No tienes permiso para modificar este proyecto", exception.getMessage());
        verify(projectRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería cambiar estado de EN_PROGRESO a TERMINADO")
    void deberiaCambiarEstadoATerminado() {
        // ARRANGE
        projectEntity.setStatus(ProjectStatus.EN_PROGRESO);
        inputDto.setStatus(ProjectStatus.TERMINADO);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(projectRepository.save(any())).thenReturn(projectEntity);
        when(projectMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        updateProjectStatusUseCase.updateStatus(projectId, userEmail, inputDto);

        // ASSERT
        assertEquals(ProjectStatus.TERMINADO, projectEntity.getStatus());
    }

    @Test
    @DisplayName("Debería cambiar estado de EN_PROGRESO a CANCELADO")
    void deberiaCambiarEstadoACancelado() {
        // ARRANGE
        projectEntity.setStatus(ProjectStatus.EN_PROGRESO);
        inputDto.setStatus(ProjectStatus.CANCELADO);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(projectRepository.save(any())).thenReturn(projectEntity);
        when(projectMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        updateProjectStatusUseCase.updateStatus(projectId, userEmail, inputDto);

        // ASSERT
        assertEquals(ProjectStatus.CANCELADO, projectEntity.getStatus());
    }

    @Test
    @DisplayName("Debería actualizar SOLO el estado, sin modificar otros campos")
    void deberiaActualizarSoloElEstado() {
        // ARRANGE
        String nombreOriginal = projectEntity.getName();
        ProjectType tipoOriginal = projectEntity.getType();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(projectRepository.save(any())).thenReturn(projectEntity);
        when(projectMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        updateProjectStatusUseCase.updateStatus(projectId, userEmail, inputDto);

        // ASSERT
        assertEquals(ProjectStatus.TERMINADO, projectEntity.getStatus(), "Estado debe cambiar");
        assertEquals(nombreOriginal, projectEntity.getName(), "Nombre NO debe cambiar");
        assertEquals(tipoOriginal, projectEntity.getType(), "Tipo NO debe cambiar");
    }

    @Test
    @DisplayName("Debería funcionar con todos los estados posibles")
    void deberiaFuncionarConTodosLosEstados() {
        // ARRANGE
        ProjectStatus[] estados = {
            ProjectStatus.EN_PROGRESO,
            ProjectStatus.TERMINADO,
            ProjectStatus.CANCELADO
        };

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(projectRepository.save(any())).thenReturn(projectEntity);
        when(projectMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT & ASSERT: Probamos cada estado
        for (ProjectStatus estado : estados) {
            inputDto.setStatus(estado);
            assertDoesNotThrow(() -> updateProjectStatusUseCase.updateStatus(projectId, userEmail, inputDto),
                "Debería funcionar con estado " + estado);
            assertEquals(estado, projectEntity.getStatus());
        }
    }

    @Test
    @DisplayName("Debería persistir el cambio de estado en el repositorio")
    void deberiaPersistirCambioDeEstado() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(projectRepository.save(any())).thenReturn(projectEntity);
        when(projectMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        updateProjectStatusUseCase.updateStatus(projectId, userEmail, inputDto);

        // ASSERT
        verify(projectRepository, times(1)).save(projectEntity);
    }
}
