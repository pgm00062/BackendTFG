package freelancer.backendtfg.ProjectTest;

import freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl.ListUserProjectsUseCaseImpl;
import freelancer.backendtfg.domain.enums.ProjectStatus;
import freelancer.backendtfg.domain.mapper.ProjectMapper;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDtoParcial;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para ListUserProjectsUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear listado paginado de proyectos de un usuario
 * - Testear filtrado por estado
 * - Testear obtención de últimos 3 proyectos
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Listar Proyectos de Usuario")
class ListUserProjectsUseCaseImplTest {

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ListUserProjectsUseCaseImpl listUserProjectsUseCase;

    private String userEmail;
    private UserEntity userEntity;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";
        pageable = PageRequest.of(0, 10);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(userEmail);
    }

    @Test
    @DisplayName("Debería listar proyectos paginados del usuario")
    void deberiaListarProyectosPaginados() {
        // ARRANGE
        ProjectEntity project1 = new ProjectEntity();
        project1.setId(1L);
        ProjectEntity project2 = new ProjectEntity();
        project2.setId(2L);

        Page<ProjectEntity> projectPage = new PageImpl<>(Arrays.asList(project1, project2));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findByUserId(anyLong(), any(Pageable.class))).thenReturn(projectPage);
        when(projectMapper.toOutputDto(any(ProjectEntity.class))).thenReturn(new ProjectOutputDto());

        // ACT
        Page<ProjectOutputDto> result = listUserProjectsUseCase.listProjects(userEmail, pageable);

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(projectRepository, times(1)).findByUserId(1L, pageable);
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class,
            () -> listUserProjectsUseCase.listProjects(userEmail, pageable));

        verify(projectRepository, never()).findByUserId(anyLong(), any());
    }

    @Test
    @DisplayName("Debería filtrar proyectos por estado")
    void deberiaFiltrarProyectosPorEstado() {
        // ARRANGE
        ProjectEntity project = new ProjectEntity();
        project.setStatus(ProjectStatus.TERMINADO);

        Page<ProjectEntity> projectPage = new PageImpl<>(Arrays.asList(project));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findByUserIdAndStatus(anyLong(), any(ProjectStatus.class), any(Pageable.class)))
            .thenReturn(projectPage);
        when(projectMapper.toOutputDto(any())).thenReturn(new ProjectOutputDto());

        // ACT
        Page<ProjectOutputDto> result = listUserProjectsUseCase.listProjectsByStatus(
            userEmail, ProjectStatus.TERMINADO, pageable);

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(projectRepository, times(1)).findByUserIdAndStatus(1L, ProjectStatus.TERMINADO, pageable);
    }

    @Test
    @DisplayName("Debería obtener los últimos 3 proyectos")
    void deberiaObtenerUltimosTresProyectos() {
        // ARRANGE
        ProjectEntity p1 = new ProjectEntity();
        ProjectEntity p2 = new ProjectEntity();
        ProjectEntity p3 = new ProjectEntity();
        List<ProjectEntity> lastProjects = Arrays.asList(p1, p2, p3);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findTop3ByUserIdOrderByCreatedAtDesc(anyLong())).thenReturn(lastProjects);
        when(projectMapper.toOutputDtoParcial(any())).thenReturn(new ProjectOutputDtoParcial());

        // ACT
        List<ProjectOutputDtoParcial> result = listUserProjectsUseCase.getLastProject(userEmail);

        // ASSERT
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(projectRepository, times(1)).findTop3ByUserIdOrderByCreatedAtDesc(1L);
        verify(projectMapper, times(3)).toOutputDtoParcial(any());
    }

    @Test
    @DisplayName("Debería retornar página vacía si el usuario no tiene proyectos")
    void deberiaRetornarPaginaVaciaSiNoHayProyectos() {
        // ARRANGE
        Page<ProjectEntity> emptyPage = new PageImpl<>(Arrays.asList());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findByUserId(anyLong(), any())).thenReturn(emptyPage);

        // ACT
        Page<ProjectOutputDto> result = listUserProjectsUseCase.listProjects(userEmail, pageable);

        // ASSERT
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debería usar el mapper para convertir cada proyecto a DTO")
    void deberiaUsarMapperParaCadaProyecto() {
        // ARRANGE
        ProjectEntity p1 = new ProjectEntity();
        ProjectEntity p2 = new ProjectEntity();
        Page<ProjectEntity> projectPage = new PageImpl<>(Arrays.asList(p1, p2));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findByUserId(anyLong(), any())).thenReturn(projectPage);
        when(projectMapper.toOutputDto(any())).thenReturn(new ProjectOutputDto());

        // ACT
        listUserProjectsUseCase.listProjects(userEmail, pageable);

        // ASSERT: El mapper se llama 2 veces (una por cada proyecto)
        verify(projectMapper, times(2)).toOutputDto(any());
    }
}
