package freelancer.backendtfg.ProjectTest;

import freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl.UpdateProjectUseCaseImpl;
import freelancer.backendtfg.domain.enums.ProjectStatus;
import freelancer.backendtfg.domain.enums.ProjectType;
import freelancer.backendtfg.domain.mapper.ProjectMapper;
import freelancer.backendtfg.infrastructure.controller.dto.input.projectsInput.ProjectCreateInputDto;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para UpdateProjectUseCaseImpl
 * 
 * LÓGICA DE NEGOCIO:
 * - Actualizar datos de un proyecto existente
 * - Solo el propietario puede modificar su proyecto
 * - Se actualizan: name, description, type, startDate, endDate, budget
 * - NO se actualiza: status (tiene su propio caso de uso)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Actualizar Proyecto")
class UpdateProjectUseCaseImplTest {

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private UpdateProjectUseCaseImpl updateProjectUseCase;

    private String userEmail;
    private Long projectId;
    private UserEntity userEntity;
    private ProjectEntity projectEntity;
    private ProjectCreateInputDto inputDto;
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
        projectEntity.setName("Proyecto Original");
        projectEntity.setDescription("Descripción original");
        projectEntity.setType(ProjectType.DESARROLLO);
        projectEntity.setStatus(ProjectStatus.EN_PROGRESO);
        projectEntity.setStartDate(LocalDate.of(2025, 1, 1));
        projectEntity.setEndDate(LocalDate.of(2025, 6, 30));
        projectEntity.setBudget(BigDecimal.valueOf(5000));
        projectEntity.setUser(userEntity);

        inputDto = new ProjectCreateInputDto();
        inputDto.setName("Proyecto Actualizado");
        inputDto.setDescription("Descripción actualizada");
        inputDto.setType(ProjectType.DISENO);
        inputDto.setStartDate(LocalDate.of(2025, 2, 1));
        inputDto.setEndDate(LocalDate.of(2025, 8, 31));
        inputDto.setBudget(BigDecimal.valueOf(7500));

        expectedOutputDto = new ProjectOutputDto();
        expectedOutputDto.setId(projectId);
        expectedOutputDto.setName("Proyecto Actualizado");
    }

    @Test
    @DisplayName("Debería actualizar proyecto correctamente cuando el usuario es propietario")
    void deberiaActualizarProyectoCorrectamente() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(projectRepository.save(any(ProjectEntity.class))).thenReturn(projectEntity);
        when(projectMapper.toOutputDto(any(ProjectEntity.class))).thenReturn(expectedOutputDto);

        // ACT
        ProjectOutputDto result = updateProjectUseCase.updateProject(projectId, userEmail, inputDto);

        // ASSERT
        assertNotNull(result);
        assertEquals("Proyecto Actualizado", projectEntity.getName());
        assertEquals("Descripción actualizada", projectEntity.getDescription());
        assertEquals(ProjectType.DISENO, projectEntity.getType());
        assertEquals(BigDecimal.valueOf(7500), projectEntity.getBudget());

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
            () -> updateProjectUseCase.updateProject(projectId, userEmail, inputDto));

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
            () -> updateProjectUseCase.updateProject(projectId, userEmail, inputDto));

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
            () -> updateProjectUseCase.updateProject(projectId, userEmail, inputDto));

        assertEquals("No tienes permiso para modificar este proyecto", exception.getMessage());
        verify(projectRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería actualizar todos los campos permitidos")
    void deberiaActualizarTodosLosCamposPermitidos() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(projectRepository.save(any())).thenReturn(projectEntity);
        when(projectMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        updateProjectUseCase.updateProject(projectId, userEmail, inputDto);

        // ASSERT: Verificamos que se actualizaron todos los campos
        assertAll("Todos los campos deberían estar actualizados",
            () -> assertEquals("Proyecto Actualizado", projectEntity.getName()),
            () -> assertEquals("Descripción actualizada", projectEntity.getDescription()),
            () -> assertEquals(ProjectType.DISENO, projectEntity.getType()),
            () -> assertEquals(LocalDate.of(2025, 2, 1), projectEntity.getStartDate()),
            () -> assertEquals(LocalDate.of(2025, 8, 31), projectEntity.getEndDate()),
            () -> assertEquals(BigDecimal.valueOf(7500), projectEntity.getBudget())
        );
    }

    @Test
    @DisplayName("No debería modificar el usuario propietario del proyecto")
    void noDeberiaModificarUsuarioPropietario() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(projectRepository.save(any())).thenReturn(projectEntity);
        when(projectMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        updateProjectUseCase.updateProject(projectId, userEmail, inputDto);

        // ASSERT
        assertEquals(userEntity, projectEntity.getUser(),
            "El usuario propietario NO debe cambiar");
    }

    @Test
    @DisplayName("Debería persistir los cambios en el repositorio")
    void deberiaPersistirCambiosEnRepositorio() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(projectEntity));
        when(projectRepository.save(any())).thenReturn(projectEntity);
        when(projectMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        updateProjectUseCase.updateProject(projectId, userEmail, inputDto);

        // ASSERT
        verify(projectRepository, times(1)).save(projectEntity);
    }
}
