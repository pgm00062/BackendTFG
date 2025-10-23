package freelancer.backendtfg.ProjectTest;

import freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl.SearchUserProjectsByNameUseCaseImpl;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para SearchUserProjectsByNameUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear búsqueda de proyectos por nombre (case insensitive)
 * - Verificar que solo busca en los proyectos del usuario autenticado
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Buscar Proyectos por Nombre")
class SearchUserProjectsByNameUseCaseImplTest {

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private SearchUserProjectsByNameUseCaseImpl searchUserProjectsByNameUseCase;

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
    @DisplayName("Debería buscar proyectos por nombre correctamente")
    void deberiaBuscarProyectosPorNombre() {
        // ARRANGE
        String searchName = "Web";
        ProjectEntity project = new ProjectEntity();
        project.setName("Proyecto Web");

        Page<ProjectEntity> projectPage = new PageImpl<>(Arrays.asList(project));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findByUserIdAndNameContainingIgnoreCase(anyLong(), anyString(), any(Pageable.class)))
            .thenReturn(projectPage);
        when(projectMapper.toOutputDto(any())).thenReturn(new ProjectOutputDto());

        // ACT
        Page<ProjectOutputDto> result = searchUserProjectsByNameUseCase.searchProjects(userEmail, searchName, pageable);

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(projectRepository, times(1)).findByUserIdAndNameContainingIgnoreCase(1L, searchName, pageable);
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class,
            () -> searchUserProjectsByNameUseCase.searchProjects(userEmail, "Web", pageable));

        verify(projectRepository, never()).findByUserIdAndNameContainingIgnoreCase(anyLong(), anyString(), any());
    }

    @Test
    @DisplayName("Debería buscar solo en proyectos del usuario autenticado")
    void deberiaBuscarSoloEnProyectosDelUsuario() {
        // ARRANGE
        Page<ProjectEntity> emptyPage = new PageImpl<>(Arrays.asList());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findByUserIdAndNameContainingIgnoreCase(anyLong(), anyString(), any()))
            .thenReturn(emptyPage);

        // ACT
        searchUserProjectsByNameUseCase.searchProjects(userEmail, "Test", pageable);

        // ASSERT: Verificamos que se filtra por el ID del usuario
        verify(projectRepository, times(1)).findByUserIdAndNameContainingIgnoreCase(
            eq(1L), anyString(), any());
    }

    @Test
    @DisplayName("Debería retornar página vacía si no hay coincidencias")
    void deberiaRetornarPaginaVaciaSiNoHayCoincidencias() {
        // ARRANGE
        Page<ProjectEntity> emptyPage = new PageImpl<>(Arrays.asList());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findByUserIdAndNameContainingIgnoreCase(anyLong(), anyString(), any()))
            .thenReturn(emptyPage);

        // ACT
        Page<ProjectOutputDto> result = searchUserProjectsByNameUseCase.searchProjects(
            userEmail, "NoExiste", pageable);

        // ASSERT
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debería usar el mapper para convertir resultados a DTO")
    void deberiaUsarMapperParaConvertir() {
        // ARRANGE
        ProjectEntity p1 = new ProjectEntity();
        ProjectEntity p2 = new ProjectEntity();
        Page<ProjectEntity> projectPage = new PageImpl<>(Arrays.asList(p1, p2));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findByUserIdAndNameContainingIgnoreCase(anyLong(), anyString(), any()))
            .thenReturn(projectPage);
        when(projectMapper.toOutputDto(any())).thenReturn(new ProjectOutputDto());

        // ACT
        searchUserProjectsByNameUseCase.searchProjects(userEmail, "Test", pageable);

        // ASSERT
        verify(projectMapper, times(2)).toOutputDto(any());
    }

    @Test
    @DisplayName("Debería buscar ignorando mayúsculas/minúsculas")
    void deberiaBuscarIgnorandoMayusculas() {
        // ARRANGE
        String[] searchTerms = {"web", "WEB", "Web", "wEb"};
        Page<ProjectEntity> page = new PageImpl<>(Arrays.asList(new ProjectEntity()));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectRepository.findByUserIdAndNameContainingIgnoreCase(anyLong(), anyString(), any()))
            .thenReturn(page);
        when(projectMapper.toOutputDto(any())).thenReturn(new ProjectOutputDto());

        // ACT & ASSERT: Todas las variantes deberían funcionar
        for (String term : searchTerms) {
            assertDoesNotThrow(() -> searchUserProjectsByNameUseCase.searchProjects(userEmail, term, pageable));
        }

        verify(projectRepository, times(4)).findByUserIdAndNameContainingIgnoreCase(anyLong(), anyString(), any());
    }
}
