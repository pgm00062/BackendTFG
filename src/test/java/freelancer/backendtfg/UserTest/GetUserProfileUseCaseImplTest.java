package freelancer.backendtfg.UserTest;

import freelancer.backendtfg.application.useCaseImpl.userUseCaseImpl.GetUserProfileUseCaseImpl;
import freelancer.backendtfg.domain.mapper.UserMapper;
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
 * Tests Unitarios para GetUserProfileUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear la obtención del perfil de un usuario por su email
 * - Verificar conversión correcta de entidad a DTO de salida
 * - Comprobar manejo de excepciones cuando el usuario no existe
 * 
 * PATRÓN:
 * - Este caso de uso es simple: busca y convierte
 * - Sin lógica de negocio compleja, perfecto para tests concisos
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Obtener Perfil de Usuario")
class GetUserProfileUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private GetUserProfileUseCaseImpl getUserProfileUseCase;

    private UserEntity userEntity;
    private UserProfileOutputDto expectedOutputDto;
    private String userEmail;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";

        // Usuario existente en la BD
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Pablo");
        userEntity.setSurname("García");
        userEntity.setEmail("pablo@test.com");
        userEntity.setDni("12345678A");
        userEntity.setPassword("$2a$10$encodedPassword");

        // DTO de salida esperado
        expectedOutputDto = new UserProfileOutputDto();
        expectedOutputDto.setId(1L);
        expectedOutputDto.setName("Pablo");
        expectedOutputDto.setSurname("García");
        expectedOutputDto.setEmail("pablo@test.com");
        expectedOutputDto.setDni("12345678A");
    }

    @Test
    @DisplayName("Debería obtener el perfil correctamente cuando el usuario existe")
    void deberiaObtenerPerfilCorrectamente() {
        // ARRANGE: El usuario existe y el mapper funciona
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(userMapper.toProfileOutputDto(any(UserEntity.class))).thenReturn(expectedOutputDto);

        // ACT: Obtenemos el perfil
        UserProfileOutputDto result = getUserProfileUseCase.getProfileByEmail(userEmail);

        // ASSERT: Verificamos el resultado
        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(1L, result.getId(), "El ID debería ser 1");
        assertEquals("Pablo", result.getName(), "El nombre debería ser Pablo");
        assertEquals("García", result.getSurname(), "El apellido debería ser García");
        assertEquals("pablo@test.com", result.getEmail(), "El email debería coincidir");
        assertEquals("12345678A", result.getDni(), "El DNI debería coincidir");

        // VERIFY: Verificamos las interacciones
        verify(userRepository, times(1)).findByEmail("pablo@test.com");
        verify(userMapper, times(1)).toProfileOutputDto(userEntity);
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE: El usuario NO existe
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT: Verificamos que se lanza la excepción
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> getUserProfileUseCase.getProfileByEmail(userEmail),
            "Debería lanzar UserNotFoundException cuando el usuario no existe"
        );

        assertEquals("Usuario no encontrado", exception.getMessage());

        // VERIFY: Verificamos que NO se llamó al mapper
        verify(userRepository, times(1)).findByEmail("pablo@test.com");
        verify(userMapper, never()).toProfileOutputDto(any());
    }

    @Test
    @DisplayName("Debería usar el mapper para convertir entidad a DTO")
    void deberiaUsarMapperParaConvertir() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(userMapper.toProfileOutputDto(userEntity)).thenReturn(expectedOutputDto);

        // ACT
        getUserProfileUseCase.getProfileByEmail(userEmail);

        // ASSERT: Verificamos que se llamó al mapper con la entidad correcta
        verify(userMapper, times(1)).toProfileOutputDto(userEntity);
    }

    @Test
    @DisplayName("Debería buscar usuario por el email proporcionado")
    void deberiaBuscarPorEmailCorrecto() {
        // ARRANGE
        String testEmail = "test@example.com";
        UserEntity testUser = new UserEntity();
        testUser.setEmail(testEmail);
        
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userMapper.toProfileOutputDto(any())).thenReturn(expectedOutputDto);

        // ACT
        getUserProfileUseCase.getProfileByEmail(testEmail);

        // ASSERT: Verificamos que se buscó por el email correcto
        verify(userRepository, times(1)).findByEmail(testEmail);
    }

    @Test
    @DisplayName("Debería retornar el DTO exacto devuelto por el mapper")
    void deberiaRetornarDtoDelMapper() {
        // ARRANGE
        UserProfileOutputDto customDto = new UserProfileOutputDto();
        customDto.setId(999L);
        customDto.setName("Test");
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(userMapper.toProfileOutputDto(any())).thenReturn(customDto);

        // ACT
        UserProfileOutputDto result = getUserProfileUseCase.getProfileByEmail(userEmail);

        // ASSERT: El resultado debería ser exactamente el que devolvió el mapper
        assertSame(customDto, result, "Debería retornar el mismo objeto que devolvió el mapper");
        assertEquals(999L, result.getId());
        assertEquals("Test", result.getName());
    }
}
