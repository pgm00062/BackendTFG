package freelancer.backendtfg.ProjectTest;

import freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl.DeleteProjectUseCaseImpl;
import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para DeleteProjectUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear la eliminación de proyectos con su lógica transaccional
 * - Verificar eliminación en cascada de tiempos asociados
 * - Validar propiedad antes de eliminar
 * 
 * IMPORTANTE:
 * - Usa @Transactional para garantizar atomicidad
 * - Primero elimina tiempos asociados, luego el proyecto
 * - Solo el propietario puede eliminar su proyecto
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Eliminar Proyecto")
class DeleteProjectUseCaseImplTest {

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private TimeRepositoryPort timeRepository;

    @InjectMocks
    private DeleteProjectUseCaseImpl deleteProjectUseCase;

    private String userEmail;
    private Long projectId;
    private UserEntity userEntity;
    private ProjectEntity projectEntity;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";
        projectId = 1L;

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(userEmail);

        projectEntity = new ProjectEntity();
        projectEntity.setId(projectId);
        projectEntity.setName("Proyecto a Eliminar");
        projectEntity.setUser(userEntity);
    }

    @Test
    @DisplayName("Debería eliminar proyecto correctamente cuando el usuario es propietario")
    void deberiaEliminarProyectoCorrectamente() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        doNothing().when(timeRepository).deleteByProjectId(anyLong());
        doNothing().when(projectRepository).delete(any(ProjectEntity.class));

        // ACT
        assertDoesNotThrow(() -> deleteProjectUseCase.deleteProject(projectId, userEmail));

        // VERIFY
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(projectRepository, times(1)).findById(projectId);
        verify(timeRepository, times(1)).deleteByProjectId(projectId);
        verify(projectRepository, times(1)).delete(projectEntity);
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class,
            () -> deleteProjectUseCase.deleteProject(projectId, userEmail));

        verify(projectRepository, never()).findById(anyLong());
        verify(timeRepository, never()).deleteByProjectId(anyLong());
        verify(projectRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException cuando el proyecto no existe")
    void deberiaLanzarExcepcionCuandoProyectoNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> deleteProjectUseCase.deleteProject(projectId, userEmail));

        assertEquals("Proyecto no encontrado", exception.getMessage());
        verify(timeRepository, never()).deleteByProjectId(anyLong());
        verify(projectRepository, never()).delete(any());
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
            () -> deleteProjectUseCase.deleteProject(projectId, userEmail));

        assertEquals("No tienes permiso para eliminar este proyecto", exception.getMessage());
        verify(timeRepository, never()).deleteByProjectId(anyLong());
        verify(projectRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debería eliminar primero los tiempos asociados, luego el proyecto")
    void deberiaEliminarTiemposPrimeroLuegoProyecto() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        doNothing().when(timeRepository).deleteByProjectId(anyLong());
        doNothing().when(projectRepository).delete(any());

        // ACT
        deleteProjectUseCase.deleteProject(projectId, userEmail);

        // ASSERT: Verificamos el orden correcto
        org.mockito.InOrder inOrder = inOrder(timeRepository, projectRepository);
        inOrder.verify(timeRepository).deleteByProjectId(projectId);  // 1. Eliminar tiempos
        inOrder.verify(projectRepository).delete(projectEntity);      // 2. Eliminar proyecto
    }

    @Test
    @DisplayName("Debería eliminar los tiempos del proyecto específico")
    void deberiaEliminarTiemposDelProyectoEspecifico() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        doNothing().when(timeRepository).deleteByProjectId(anyLong());
        doNothing().when(projectRepository).delete(any());

        // ACT
        deleteProjectUseCase.deleteProject(projectId, userEmail);

        // ASSERT: Verificamos que se eliminan los tiempos con el ID correcto
        verify(timeRepository, times(1)).deleteByProjectId(projectId);
    }

    @Test
    @DisplayName("Debería eliminar exactamente el proyecto encontrado")
    void deberiaEliminarExactamenteElProyectoEncontrado() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        doNothing().when(timeRepository).deleteByProjectId(anyLong());
        doNothing().when(projectRepository).delete(any());

        // ACT
        deleteProjectUseCase.deleteProject(projectId, userEmail);

        // ASSERT
        verify(projectRepository, times(1)).delete(projectEntity);
    }

    @Test
    @DisplayName("No debería ejecutar eliminaciones si la validación de usuario falla")
    void noDeberiaEliminarSiValidacionDUsuarioFalla() {
        // ARRANGE: Usuario no es propietario
        UserEntity otroUsuario = new UserEntity();
        otroUsuario.setId(2L);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(otroUsuario));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));

        // ACT
        try {
            deleteProjectUseCase.deleteProject(projectId, userEmail);
        } catch (RuntimeException e) {
            // Excepción esperada
        }

        // ASSERT: NUNCA se debe llamar a los métodos de eliminación
        verify(timeRepository, never()).deleteByProjectId(anyLong());
        verify(projectRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debería validar todas las condiciones antes de eliminar")
    void deberiaValidarTodasLasCondicionesAntesDeEliminar() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        doNothing().when(timeRepository).deleteByProjectId(anyLong());
        doNothing().when(projectRepository).delete(any());

        // ACT
        deleteProjectUseCase.deleteProject(projectId, userEmail);

        // ASSERT: Verificamos el flujo completo
        org.mockito.InOrder inOrder = inOrder(userRepository, projectRepository, timeRepository);
        inOrder.verify(userRepository).findByEmail(userEmail);       // 1. Validar usuario existe
        inOrder.verify(projectRepository).findById(projectId);       // 2. Validar proyecto existe
        // 3. Validar propiedad (se hace en código, no podemos verificar directamente)
        inOrder.verify(timeRepository).deleteByProjectId(projectId); // 4. Eliminar tiempos
        inOrder.verify(projectRepository).delete(projectEntity);     // 5. Eliminar proyecto
    }
}
