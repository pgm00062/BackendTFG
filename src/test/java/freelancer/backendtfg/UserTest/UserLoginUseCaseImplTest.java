package freelancer.backendtfg.UserTest;

import freelancer.backendtfg.application.useCaseImpl.userUseCaseImpl.UserLoginUseCaseImpl;
import freelancer.backendtfg.domain.mapper.UserMapper;
import freelancer.backendtfg.infrastructure.controller.dto.input.usersInput.UserLoginInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput.UserLoginOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.InvalidCredentialsException;
import freelancer.backendtfg.shared.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para UserLoginUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear la lógica de autenticación de usuarios
 * - Verificar validación de credenciales (email + contraseña)
 * - Comprobar generación de token JWT
 * 
 * CASOS DE USO CUBIERTOS:
 * - Login exitoso con credenciales correctas
 * - Login fallido por usuario no encontrado
 * - Login fallido por contraseña incorrecta
 * - Generación correcta del token JWT
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Login de Usuario")
class UserLoginUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserLoginUseCaseImpl userLoginUseCase;

    private UserLoginInputDto validLoginDto;
    private UserEntity userEntity;
    private String mockToken;

    @BeforeEach
    void setUp() {
        // Datos de entrada válidos
        validLoginDto = new UserLoginInputDto();
        validLoginDto.setEmail("pablo@test.com");
        validLoginDto.setPassword("password123");

        // Usuario existente en la BD (con contraseña ya encriptada)
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Pablo");
        userEntity.setSurname("García");
        userEntity.setEmail("pablo@test.com");
        userEntity.setPassword("$2a$10$encodedPassword");

        // Token JWT simulado
        mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mockToken";
    }

    @Test
    @DisplayName("Debería autenticar correctamente con credenciales válidas")
    void deberiaAutenticarCorrectamente() {
        // ARRANGE: Configuramos el comportamiento esperado
        // 1. El usuario existe en la BD
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        
        // 2. La contraseña coincide (passwordEncoder.matches devuelve true)
        when(passwordEncoder.matches("password123", userEntity.getPassword())).thenReturn(true);
        
        // 3. JwtUtils genera un token
        when(jwtUtils.generateToken(anyString())).thenReturn(mockToken);

        // ACT: Ejecutamos el login
        UserLoginOutputDto result = userLoginUseCase.login(validLoginDto);

        // ASSERT: Verificamos el resultado
        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(1L, result.getId(), "El ID del usuario debería ser 1");
        assertEquals("Pablo", result.getName(), "El nombre debería ser Pablo");
        assertEquals("García", result.getSurname(), "El apellido debería ser García");
        assertEquals("pablo@test.com", result.getEmail(), "El email debería coincidir");
        assertEquals(mockToken, result.getToken(), "El token JWT debería estar presente");

        // VERIFY: Verificamos las interacciones
        verify(userRepository, times(1)).findByEmail("pablo@test.com");
        verify(passwordEncoder, times(1)).matches("password123", "$2a$10$encodedPassword");
        verify(jwtUtils, times(1)).generateToken("pablo@test.com");
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE: El usuario NO existe en la BD
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT: Verificamos que se lanza la excepción
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> userLoginUseCase.login(validLoginDto),
            "Debería lanzar RuntimeException cuando el usuario no existe"
        );

        assertEquals("Usuario no encontrado", exception.getMessage());

        // VERIFY: Verificamos que NO se validó la contraseña
        verify(userRepository, times(1)).findByEmail("pablo@test.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtils, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Debería lanzar InvalidCredentialsException cuando la contraseña es incorrecta")
    void deberiaLanzarExcepcionCuandoContrasenaIncorrecta() {
        // ARRANGE: El usuario existe pero la contraseña no coincide
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // ACT & ASSERT: Verificamos que se lanza la excepción
        InvalidCredentialsException exception = assertThrows(
            InvalidCredentialsException.class,
            () -> userLoginUseCase.login(validLoginDto),
            "Debería lanzar InvalidCredentialsException con contraseña incorrecta"
        );

        assertEquals("Contraseña incorrecta", exception.getMessage());

        // VERIFY: Verificamos que se buscó el usuario y se validó la contraseña
        verify(userRepository, times(1)).findByEmail("pablo@test.com");
        verify(passwordEncoder, times(1)).matches("password123", "$2a$10$encodedPassword");
        verify(jwtUtils, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Debería generar token JWT con el email del usuario")
    void deberiaGenerarTokenConEmailDelUsuario() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtils.generateToken("pablo@test.com")).thenReturn(mockToken);

        // ACT
        UserLoginOutputDto result = userLoginUseCase.login(validLoginDto);

        // ASSERT: Verificamos que se generó el token con el email correcto
        verify(jwtUtils, times(1)).generateToken("pablo@test.com");
        assertEquals(mockToken, result.getToken());
    }

    @Test
    @DisplayName("Debería construir UserLoginOutputDto con todos los datos del usuario")
    void deberiaConstruirOutputDtoConTodosLosDatos() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtils.generateToken(anyString())).thenReturn(mockToken);

        // ACT
        UserLoginOutputDto result = userLoginUseCase.login(validLoginDto);

        // ASSERT: Verificamos que el DTO tiene TODOS los campos esperados
        // NOTA: No usamos mapper aquí, el caso de uso construye el DTO manualmente
        assertAll("UserLoginOutputDto debería tener todos los campos",
            () -> assertEquals(1L, result.getId()),
            () -> assertEquals("Pablo", result.getName()),
            () -> assertEquals("García", result.getSurname()),
            () -> assertEquals("pablo@test.com", result.getEmail()),
            () -> assertEquals(mockToken, result.getToken())
        );
    }

    @Test
    @DisplayName("Debería validar contraseña usando BCryptPasswordEncoder.matches")
    void deberiaValidarContrasenaConBCrypt() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("password123", "$2a$10$encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken(anyString())).thenReturn(mockToken);

        // ACT
        userLoginUseCase.login(validLoginDto);

        // ASSERT: Verificamos que se usó matches y NO equals
        // matches compara la contraseña plana con la hash
        verify(passwordEncoder, times(1)).matches("password123", "$2a$10$encodedPassword");
    }
}
