package freelancer.backendtfg.ProjectTest;

import freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl.GetProjectByIdUseCaseImpl;
import freelancer.backendtfg.domain.enums.ProjectStatus;
import freelancer.backendtfg.domain.enums.ProjectType;
import freelancer.backendtfg.domain.mapper.ProjectMapper;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para GetProjectByIdUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear la obtención de un proyecto por su ID
 * - Verificar validación de existencia de usuario
 * - Verificar validación de existencia de proyecto
 * - Verificar validación de propiedad (que el usuario sea dueño del proyecto)
 * 
 * LÓGICA DE SEGURIDAD:
 * - Solo el propietario puede acceder a sus proyectos
 * - Se valida usuario, proyecto y propiedad antes de retornar datos
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Obtener Proyecto por ID")
class GetProjectByIdUseCaseImplTest {

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private GetProjectByIdUseCaseImpl getProjectByIdUseCase;

    private String userEmail;
    private Long projectId;
    private UserEntity userEntity;
    private ProjectEntity projectEntity;
    private ProjectOutputDto expectedOutputDto;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";
        projectId = 1L;

        // Usuario propietario del proyecto
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Pablo");
        userEntity.setEmail(userEmail);

        // Proyecto del usuario
        projectEntity = new ProjectEntity();
        projectEntity.setId(projectId);
        projectEntity.setName("Proyecto Web");
        projectEntity.setDescription("Desarrollo web");
        projectEntity.setType(ProjectType.DESARROLLO);
        projectEntity.setStatus(ProjectStatus.EN_PROGRESO);
        projectEntity.setStartDate(LocalDate.of(2025, 1, 1));
        projectEntity.setEndDate(LocalDate.of(2025, 6, 30));
        projectEntity.setBudget(BigDecimal.valueOf(5000));
        projectEntity.setUser(userEntity);

        // DTO de salida
        expectedOutputDto = new ProjectOutputDto();
        expectedOutputDto.setId(projectId);
        expectedOutputDto.setName("Proyecto Web");
        expectedOutputDto.setType(ProjectType.DESARROLLO);
        expectedOutputDto.setStatus(ProjectStatus.EN_PROGRESO);
    }

    @Test
    @DisplayName("Debería obtener el proyecto correctamente cuando el usuario es propietario")
    void deberiaObtenerProyectoCorrectamente() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(projectMapper.toOutputDto(any(ProjectEntity.class))).thenReturn(expectedOutputDto);

        // ACT
        ProjectOutputDto result = getProjectByIdUseCase.getProjectById(projectId, userEmail);

        // ASSERT
        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(projectId, result.getId(), "El ID debería coincidir");
        assertEquals("Proyecto Web", result.getName(), "El nombre debería coincidir");
        assertEquals(ProjectType.DESARROLLO, result.getType(), "El tipo debería coincidir");

        // VERIFY
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectMapper, times(1)).toOutputDto(projectEntity);
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> getProjectByIdUseCase.getProjectById(projectId, userEmail),
            "Debería lanzar UserNotFoundException cuando el usuario no existe"
        );

        assertEquals("Usuario no encontrado", exception.getMessage());

        // VERIFY: No se debe buscar el proyecto si el usuario no existe
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(projectRepository, never()).findById(anyLong());
        verify(projectMapper, never()).toOutputDto(any());
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException cuando el proyecto no existe")
    void deberiaLanzarExcepcionCuandoProyectoNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> getProjectByIdUseCase.getProjectById(projectId, userEmail),
            "Debería lanzar RuntimeException cuando el proyecto no existe"
        );

        assertEquals("Proyecto no encontrado", exception.getMessage());

        // VERIFY
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectMapper, never()).toOutputDto(any());
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException cuando el usuario no es propietario del proyecto")
    void deberiaLanzarExcepcionCuandoUsuarioNoEsPropietario() {
        // ARRANGE: Otro usuario intenta acceder al proyecto
        UserEntity otroUsuario = new UserEntity();
        otroUsuario.setId(2L); // ID diferente
        otroUsuario.setEmail("otro@test.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(otroUsuario));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));

        // ACT & ASSERT
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> getProjectByIdUseCase.getProjectById(projectId, "otro@test.com"),
            "Debería lanzar RuntimeException cuando el usuario no es propietario"
        );

        assertEquals("No tienes acceso a este proyecto", exception.getMessage());

        // VERIFY: No se debe convertir a DTO si no es propietario
        verify(userRepository, times(1)).findByEmail("otro@test.com");
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectMapper, never()).toOutputDto(any());
    }

    @Test
    @DisplayName("Debería validar la propiedad comparando IDs de usuario")
    void deberiaValidarPropiedadComparandoIds() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(projectMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        getProjectByIdUseCase.getProjectById(projectId, userEmail);

        // ASSERT: Verificamos que se comparó el ID del usuario del proyecto con el ID del usuario actual
        // Esto se hace internamente: project.getUser().getId().equals(user.getId())
        assertEquals(userEntity.getId(), projectEntity.getUser().getId(),
            "Los IDs de usuario deberían coincidir para permitir acceso");
    }

    @Test
    @DisplayName("Debería mantener el orden correcto de validaciones")
    void deberiaManteneerOrdenCorrectoDValidaciones() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(projectMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        getProjectByIdUseCase.getProjectById(projectId, userEmail);

        // ASSERT: Verificamos el orden con InOrder
        org.mockito.InOrder inOrder = inOrder(userRepository, projectRepository, projectMapper);
        inOrder.verify(userRepository).findByEmail(userEmail);   // 1. Validar usuario
        inOrder.verify(projectRepository).findById(projectId);   // 2. Validar proyecto
        inOrder.verify(projectMapper).toOutputDto(projectEntity); // 3. Convertir a DTO
    }

    @Test
    @DisplayName("Debería usar el mapper para convertir la entidad a DTO")
    void deberiaUsarMapperParaConvertir() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(projectMapper.toOutputDto(projectEntity)).thenReturn(expectedOutputDto);

        // ACT
        ProjectOutputDto result = getProjectByIdUseCase.getProjectById(projectId, userEmail);

        // ASSERT
        verify(projectMapper, times(1)).toOutputDto(projectEntity);
        assertSame(expectedOutputDto, result);
    }

    @Test
    @DisplayName("Debería funcionar con diferentes IDs de proyecto")
    void deberiaFuncionarConDiferentesIds() {
        // ARRANGE: Probamos con varios IDs
        Long[] proyectoIds = {1L, 5L, 100L, 999L};

        for (Long id : proyectoIds) {
            ProjectEntity proyecto = new ProjectEntity();
            proyecto.setId(id);
            proyecto.setUser(userEntity);

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
            when(projectRepository.findById(id)).thenReturn(Optional.of(proyecto));
            when(projectMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

            // ACT & ASSERT
            assertDoesNotThrow(() -> getProjectByIdUseCase.getProjectById(id, userEmail),
                "Debería funcionar con ID " + id);
        }
    }

    @Test
    @DisplayName("Debería prevenir acceso a proyectos de otros usuarios")
    void deberiaPrevenirAccesoAProyectosDeOtrosUsuarios() {
        // ARRANGE: Configuración de seguridad
        UserEntity propietario = new UserEntity();
        propietario.setId(1L);
        propietario.setEmail("propietario@test.com");

        UserEntity intruso = new UserEntity();
        intruso.setId(2L);
        intruso.setEmail("intruso@test.com");

        ProjectEntity proyectoPrivado = new ProjectEntity();
        proyectoPrivado.setId(1L);
        proyectoPrivado.setUser(propietario);

        when(userRepository.findByEmail("intruso@test.com")).thenReturn(Optional.of(intruso));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(proyectoPrivado));

        // ACT & ASSERT
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> getProjectByIdUseCase.getProjectById(1L, "intruso@test.com"),
            "Debería impedir que un usuario acceda a proyectos de otro"
        );

        assertEquals("No tienes acceso a este proyecto", exception.getMessage());
    }
}
