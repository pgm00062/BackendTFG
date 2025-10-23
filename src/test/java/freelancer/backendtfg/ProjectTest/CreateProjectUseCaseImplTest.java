package freelancer.backendtfg.ProjectTest;

import freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl.CreateProjectUseCaseImpl;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para CreateProjectUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear la creación de proyectos asociados a un usuario
 * - Verificar que se valida la existencia del usuario
 * - Comprobar que se asocia correctamente el usuario al proyecto
 * - Validar la conversión de DTOs a entidades
 * 
 * LÓGICA DE NEGOCIO:
 * 1. Buscar usuario por email
 * 2. Convertir DTO de entrada a entidad
 * 3. Asociar usuario al proyecto
 * 4. Guardar proyecto
 * 5. Retornar DTO de salida
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Crear Proyecto")
class CreateProjectUseCaseImplTest {

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private CreateProjectUseCaseImpl createProjectUseCase;

    private String userEmail;
    private UserEntity userEntity;
    private ProjectCreateInputDto inputDto;
    private ProjectEntity projectEntity;
    private ProjectEntity savedProjectEntity;
    private ProjectOutputDto expectedOutputDto;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";

        // Usuario existente
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Pablo");
        userEntity.setEmail(userEmail);

        // DTO de entrada para crear proyecto
        inputDto = new ProjectCreateInputDto();
        inputDto.setName("Proyecto Web");
        inputDto.setDescription("Desarrollo de aplicación web");
        inputDto.setType(ProjectType.DESARROLLO);
        inputDto.setStartDate(LocalDate.of(2025, 1, 1));
        inputDto.setEndDate(LocalDate.of(2025, 6, 30));
        inputDto.setBudget(BigDecimal.valueOf(5000));

        // Entidad del proyecto (antes de guardar)
        projectEntity = new ProjectEntity();
        projectEntity.setName("Proyecto Web");
        projectEntity.setDescription("Desarrollo de aplicación web");
        projectEntity.setType(ProjectType.DESARROLLO);
        projectEntity.setStatus(ProjectStatus.EN_PROGRESO);
        projectEntity.setStartDate(LocalDate.of(2025, 1, 1));
        projectEntity.setEndDate(LocalDate.of(2025, 6, 30));
        projectEntity.setBudget(BigDecimal.valueOf(5000));

        // Entidad del proyecto guardada (con ID)
        savedProjectEntity = new ProjectEntity();
        savedProjectEntity.setId(1L);
        savedProjectEntity.setName("Proyecto Web");
        savedProjectEntity.setDescription("Desarrollo de aplicación web");
        savedProjectEntity.setType(ProjectType.DESARROLLO);
        savedProjectEntity.setStatus(ProjectStatus.EN_PROGRESO);
        savedProjectEntity.setStartDate(LocalDate.of(2025, 1, 1));
        savedProjectEntity.setEndDate(LocalDate.of(2025, 6, 30));
        savedProjectEntity.setBudget(BigDecimal.valueOf(5000));
        savedProjectEntity.setUser(userEntity);

        // DTO de salida
        expectedOutputDto = new ProjectOutputDto();
        expectedOutputDto.setId(1L);
        expectedOutputDto.setName("Proyecto Web");
        expectedOutputDto.setDescription("Desarrollo de aplicación web");
        expectedOutputDto.setType(ProjectType.DESARROLLO);
        expectedOutputDto.setStatus(ProjectStatus.EN_PROGRESO);
    }

    @Test
    @DisplayName("Debería crear proyecto correctamente con datos válidos")
    void deberiaCrearProyectoCorrectamente() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectMapper.toEntity(any(ProjectCreateInputDto.class))).thenReturn(projectEntity);
        when(projectRepository.save(any(ProjectEntity.class))).thenReturn(savedProjectEntity);
        when(projectMapper.toOutputDto(any(ProjectEntity.class))).thenReturn(expectedOutputDto);

        // ACT
        ProjectOutputDto result = createProjectUseCase.createProject(userEmail, inputDto);

        // ASSERT
        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(1L, result.getId(), "El ID debería ser 1");
        assertEquals("Proyecto Web", result.getName(), "El nombre debería coincidir");
        assertEquals(ProjectType.DESARROLLO, result.getType(), "El tipo debería ser DESARROLLO");
        assertEquals(ProjectStatus.EN_PROGRESO, result.getStatus(), "El estado debería ser EN_PROGRESO");

        // VERIFY
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(projectMapper, times(1)).toEntity(inputDto);
        verify(projectRepository, times(1)).save(any(ProjectEntity.class));
        verify(projectMapper, times(1)).toOutputDto(savedProjectEntity);
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> createProjectUseCase.createProject(userEmail, inputDto),
            "Debería lanzar UserNotFoundException cuando el usuario no existe"
        );

        assertEquals("Usuario no encontrado", exception.getMessage());

        // VERIFY: No se debe intentar crear el proyecto
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(projectMapper, never()).toEntity(any());
        verify(projectRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería asociar el usuario al proyecto antes de guardar")
    void deberiaAsociarUsuarioAlProyecto() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectMapper.toEntity(any(ProjectCreateInputDto.class))).thenReturn(projectEntity);
        when(projectRepository.save(any(ProjectEntity.class))).thenAnswer(invocation -> {
            ProjectEntity savedProject = invocation.getArgument(0);
            // Verificamos que el usuario fue asociado antes de guardar
            assertEquals(userEntity, savedProject.getUser(), 
                "El usuario debería estar asociado al proyecto");
            return savedProjectEntity;
        });
        when(projectMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        createProjectUseCase.createProject(userEmail, inputDto);

        // ASSERT: Verificamos que se asoció el usuario
        verify(projectRepository, times(1)).save(any(ProjectEntity.class));
    }

    @Test
    @DisplayName("Debería convertir el DTO de entrada a entidad usando el mapper")
    void deberiaConvertirDtoAEntidadConMapper() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectMapper.toEntity(inputDto)).thenReturn(projectEntity);
        when(projectRepository.save(any())).thenReturn(savedProjectEntity);
        when(projectMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        createProjectUseCase.createProject(userEmail, inputDto);

        // ASSERT
        verify(projectMapper, times(1)).toEntity(inputDto);
    }

    @Test
    @DisplayName("Debería guardar el proyecto en el repositorio")
    void deberiaGuardarProyectoEnRepositorio() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectMapper.toEntity(any())).thenReturn(projectEntity);
        when(projectRepository.save(any(ProjectEntity.class))).thenReturn(savedProjectEntity);
        when(projectMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        createProjectUseCase.createProject(userEmail, inputDto);

        // ASSERT
        verify(projectRepository, times(1)).save(projectEntity);
    }

    @Test
    @DisplayName("Debería retornar el DTO de salida generado por el mapper")
    void deberiaRetornarDtoGeneradoPorMapper() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectMapper.toEntity(any())).thenReturn(projectEntity);
        when(projectRepository.save(any())).thenReturn(savedProjectEntity);
        when(projectMapper.toOutputDto(savedProjectEntity)).thenReturn(expectedOutputDto);

        // ACT
        ProjectOutputDto result = createProjectUseCase.createProject(userEmail, inputDto);

        // ASSERT
        assertSame(expectedOutputDto, result, 
            "Debería retornar el mismo DTO que devolvió el mapper");
        verify(projectMapper, times(1)).toOutputDto(savedProjectEntity);
    }

    @Test
    @DisplayName("Debería mantener el orden correcto de operaciones")
    void deberiaManteneerOrdenCorrectoDOperaciones() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectMapper.toEntity(any())).thenReturn(projectEntity);
        when(projectRepository.save(any())).thenReturn(savedProjectEntity);
        when(projectMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        createProjectUseCase.createProject(userEmail, inputDto);

        // ASSERT: Verificamos el orden con InOrder
        org.mockito.InOrder inOrder = inOrder(userRepository, projectMapper, projectRepository);
        inOrder.verify(userRepository).findByEmail(userEmail);  // 1. Buscar usuario
        inOrder.verify(projectMapper).toEntity(inputDto);       // 2. Convertir DTO a entidad
        inOrder.verify(projectRepository).save(any());          // 3. Guardar proyecto
        inOrder.verify(projectMapper).toOutputDto(any());       // 4. Convertir a DTO de salida
    }

    @Test
    @DisplayName("Debería crear proyecto con diferentes tipos")
    void deberiaCrearProyectoConDiferentesTipos() {
        // ARRANGE: Probamos con diferentes tipos de proyecto
        ProjectType[] tipos = {ProjectType.DESARROLLO, ProjectType.DISENO, ProjectType.CONSULTORIA};
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(projectMapper.toEntity(any())).thenReturn(projectEntity);
        when(projectRepository.save(any())).thenReturn(savedProjectEntity);
        when(projectMapper.toOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT & ASSERT: Probamos cada tipo
        for (ProjectType tipo : tipos) {
            inputDto.setType(tipo);
            assertDoesNotThrow(() -> createProjectUseCase.createProject(userEmail, inputDto),
                "Debería crear proyecto con tipo " + tipo);
        }

        // Verificamos que se crearon 3 proyectos
        verify(projectRepository, times(3)).save(any());
    }
}
