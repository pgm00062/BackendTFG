package freelancer.backendtfg.UserTest;

import freelancer.backendtfg.application.useCaseImpl.userUseCaseImpl.UpdateUserProfileUseCaseImpl;
import freelancer.backendtfg.domain.mapper.UserMapper;
import freelancer.backendtfg.infrastructure.controller.dto.input.usersInput.UpdateUserProfileInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput.UserProfileOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para UpdateUserProfileUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear la actualización del perfil de usuario
 * - Verificar que se modifican los campos correctos (name, surname, dni)
 * - Comprobar que se persisten los cambios en el repositorio
 * - Validar manejo de excepciones cuando el usuario no existe
 * 
 * LÓGICA IMPORTANTE:
 * - Solo se actualizan ciertos campos (NO email, NO password)
 * - Se debe buscar el usuario, modificarlo y guardarlo
 * - El resultado debe ser el usuario actualizado convertido a DTO
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Actualizar Perfil de Usuario")
class UpdateUserProfileUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UpdateUserProfileUseCaseImpl updateUserProfileUseCase;

    private String userEmail;
    private UpdateUserProfileInputDto inputDto;
    private UserEntity originalUserEntity;
    private UserEntity updatedUserEntity;
    private UserProfileOutputDto expectedOutputDto;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";

        // DTO con los nuevos datos a actualizar
        inputDto = new UpdateUserProfileInputDto();
        inputDto.setName("Pablo Actualizado");
        inputDto.setSurname("García Nuevo");
        inputDto.setDni("87654321B");

        // Usuario original en la BD (antes de actualizar)
        originalUserEntity = new UserEntity();
        originalUserEntity.setId(1L);
        originalUserEntity.setName("Pablo");
        originalUserEntity.setSurname("García");
        originalUserEntity.setEmail("pablo@test.com");
        originalUserEntity.setDni("12345678A");
        originalUserEntity.setPassword("$2a$10$encodedPassword");

        // Usuario después de la actualización (con campos modificados)
        updatedUserEntity = new UserEntity();
        updatedUserEntity.setId(1L);
        updatedUserEntity.setName("Pablo Actualizado");
        updatedUserEntity.setSurname("García Nuevo");
        updatedUserEntity.setEmail("pablo@test.com"); // Email NO cambia
        updatedUserEntity.setDni("87654321B");
        updatedUserEntity.setPassword("$2a$10$encodedPassword"); // Password NO cambia

        // DTO de salida esperado
        expectedOutputDto = new UserProfileOutputDto();
        expectedOutputDto.setId(1L);
        expectedOutputDto.setName("Pablo Actualizado");
        expectedOutputDto.setSurname("García Nuevo");
        expectedOutputDto.setEmail("pablo@test.com");
        expectedOutputDto.setDni("87654321B");
    }

    @Test
    @DisplayName("Debería actualizar el perfil correctamente con datos válidos")
    void deberiaActualizarPerfilCorrectamente() {
        // ARRANGE: Configuramos el comportamiento de los mocks
        // 1. El usuario existe
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(originalUserEntity));
        
        // 2. El repositorio guarda y devuelve el usuario actualizado
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedUserEntity);
        
        // 3. El mapper convierte la entidad actualizada a DTO
        when(userMapper.toProfileOutputDto(any(UserEntity.class))).thenReturn(expectedOutputDto);

        // ACT: Actualizamos el perfil
        UserProfileOutputDto result = updateUserProfileUseCase.updateProfile(userEmail, inputDto);

        // ASSERT: Verificamos el resultado
        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(1L, result.getId(), "El ID no debería cambiar");
        assertEquals("Pablo Actualizado", result.getName(), "El nombre debería estar actualizado");
        assertEquals("García Nuevo", result.getSurname(), "El apellido debería estar actualizado");
        assertEquals("87654321B", result.getDni(), "El DNI debería estar actualizado");
        assertEquals("pablo@test.com", result.getEmail(), "El email NO debería cambiar");

        // VERIFY: Verificamos las interacciones
        verify(userRepository, times(1)).findByEmail("pablo@test.com");
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userMapper, times(1)).toProfileOutputDto(updatedUserEntity);
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE: El usuario NO existe
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT: Verificamos que se lanza la excepción
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> updateUserProfileUseCase.updateProfile(userEmail, inputDto),
            "Debería lanzar UserNotFoundException cuando el usuario no existe"
        );

        assertEquals("Usuario no encontrado", exception.getMessage());

        // VERIFY: Verificamos que NO se intentó guardar
        verify(userRepository, times(1)).findByEmail("pablo@test.com");
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toProfileOutputDto(any());
    }

    @Test
    @DisplayName("Debería modificar solo los campos permitidos (name, surname, dni)")
    void deberiaModificarSoloCamposPermitidos() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(originalUserEntity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toProfileOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        updateUserProfileUseCase.updateProfile(userEmail, inputDto);

        // ASSERT: Verificamos que la entidad tiene los valores actualizados
        // NOTA: El caso de uso modifica directamente la entidad con setters
        assertEquals("Pablo Actualizado", originalUserEntity.getName());
        assertEquals("García Nuevo", originalUserEntity.getSurname());
        assertEquals("87654321B", originalUserEntity.getDni());
        
        // Y que NO se modificaron estos campos:
        assertEquals("pablo@test.com", originalUserEntity.getEmail(), "Email NO debe cambiar");
        assertEquals("$2a$10$encodedPassword", originalUserEntity.getPassword(), "Password NO debe cambiar");
    }

    @Test
    @DisplayName("Debería persistir los cambios en el repositorio")
    void deberiaPersistirCambiosEnRepositorio() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(originalUserEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedUserEntity);
        when(userMapper.toProfileOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        updateUserProfileUseCase.updateProfile(userEmail, inputDto);

        // ASSERT: Verificamos que se llamó a save con la entidad modificada
        verify(userRepository, times(1)).save(originalUserEntity);
    }

    @Test
    @DisplayName("Debería retornar el usuario actualizado como DTO")
    void deberiaRetornarUsuarioActualizadoComoDto() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(originalUserEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedUserEntity);
        when(userMapper.toProfileOutputDto(updatedUserEntity)).thenReturn(expectedOutputDto);

        // ACT
        UserProfileOutputDto result = updateUserProfileUseCase.updateProfile(userEmail, inputDto);

        // ASSERT: Verificamos que el mapper se llamó con el usuario actualizado
        verify(userMapper, times(1)).toProfileOutputDto(updatedUserEntity);
        assertSame(expectedOutputDto, result);
    }

    @Test
    @DisplayName("Debería buscar usuario por email antes de actualizar")
    void deberiaBuscarPorEmailAntesDeActualizar() {
        // ARRANGE
        String customEmail = "custom@test.com";
        UserEntity customUser = new UserEntity();
        customUser.setEmail(customEmail);
        
        when(userRepository.findByEmail(customEmail)).thenReturn(Optional.of(customUser));
        when(userRepository.save(any())).thenReturn(customUser);
        when(userMapper.toProfileOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        updateUserProfileUseCase.updateProfile(customEmail, inputDto);

        // ASSERT: Verificamos que se buscó por el email correcto
        verify(userRepository, times(1)).findByEmail(customEmail);
    }

    @Test
    @DisplayName("Debería aplicar todos los cambios del inputDto")
    void deberiaAplicarTodosLosCambios() {
        // ARRANGE
        UpdateUserProfileInputDto multipleChanges = new UpdateUserProfileInputDto();
        multipleChanges.setName("Nuevo Nombre");
        multipleChanges.setSurname("Nuevo Apellido");
        multipleChanges.setDni("11111111X");
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(originalUserEntity));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toProfileOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        updateUserProfileUseCase.updateProfile(userEmail, multipleChanges);

        // ASSERT: Verificamos que TODOS los campos se actualizaron
        assertAll("Todos los campos deberían estar actualizados",
            () -> assertEquals("Nuevo Nombre", originalUserEntity.getName()),
            () -> assertEquals("Nuevo Apellido", originalUserEntity.getSurname()),
            () -> assertEquals("11111111X", originalUserEntity.getDni())
        );
    }
}
