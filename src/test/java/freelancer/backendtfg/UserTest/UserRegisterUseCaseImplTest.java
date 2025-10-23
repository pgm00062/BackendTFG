package freelancer.backendtfg.UserTest;

import freelancer.backendtfg.application.useCaseImpl.userUseCaseImpl.UserRegisterUseCaseImpl;
import freelancer.backendtfg.domain.mapper.UserMapper;
import freelancer.backendtfg.infrastructure.controller.dto.input.usersInput.UserRegisterInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput.UserRegisterOutputDto;
import freelancer.backendtfg.infrastructure.repository.UserRepository;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.shared.exceptions.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para UserRegisterUseCaseImpl
 * 
 * FILOSOFÍA:
 * - Sin @SpringBootTest: No levantamos el contexto de Spring
 * - Con @ExtendWith(MockitoExtension.class): Activamos Mockito para crear mocks
 * - Testeamos SOLO la lógica de negocio de forma aislada
 * 
 * DEPENDENCIAS MOCKEADAS:
 * - UserRepository: No queremos acceder a la BD real
 * - UserMapper: Simulamos la conversión de DTOs a entidades
 * - BCryptPasswordEncoder: Simulamos el encriptado de contraseñas
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Registro de Usuario")
class UserRegisterUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRegisterUseCaseImpl userRegisterUseCase;

    private UserRegisterInputDto validInputDto;
    private UserEntity userEntity;
    private UserRegisterOutputDto expectedOutputDto;

    @BeforeEach
    void setUp() {
        // ARRANGE: Preparamos datos de prueba reutilizables
        validInputDto = new UserRegisterInputDto();
        validInputDto.setName("Pablo");
        validInputDto.setSurname("García");
        validInputDto.setEmail("pablo@test.com");
        validInputDto.setDni("12345678A");
        validInputDto.setPassword("password123");

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Pablo");
        userEntity.setSurname("García");
        userEntity.setEmail("pablo@test.com");
        userEntity.setDni("12345678A");
        userEntity.setPassword("$2a$10$encodedPassword");

        expectedOutputDto = new UserRegisterOutputDto();
        expectedOutputDto.setId(1L);
        expectedOutputDto.setName("Pablo");
        expectedOutputDto.setEmail("pablo@test.com");
    }

    @Test
    @DisplayName("Debería registrar usuario correctamente con datos válidos")
    void deberiaRegistrarUsuarioCorrectamente() {
        // ARRANGE: Configuramos el comportamiento de los mocks
        // 1. El email NO existe en la BD
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        
        // 2. El DNI NO existe en la BD
        when(userRepository.existsByDni(anyString())).thenReturn(false);
        
        // 3. El encoder devuelve una contraseña encriptada
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        
        // 4. El mapper convierte el DTO a entidad
        when(userMapper.toEntity(any(UserRegisterInputDto.class))).thenReturn(userEntity);
        
        // 5. El repositorio guarda y devuelve la entidad
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        
        // 6. El mapper convierte la entidad a DTO de salida
        when(userMapper.toRegisterOutputDto(any(UserEntity.class))).thenReturn(expectedOutputDto);

        // ACT: Ejecutamos el método bajo test
        UserRegisterOutputDto result = userRegisterUseCase.register(validInputDto);

        // ASSERT: Verificamos el resultado
        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(1L, result.getId(), "El ID del usuario debería ser 1");
        assertEquals("Pablo", result.getName(), "El nombre debería ser Pablo");
        assertEquals("pablo@test.com", result.getEmail(), "El email debería coincidir");

        // VERIFY: Verificamos las interacciones con los mocks
        verify(userRepository, times(1)).existsByEmail("pablo@test.com");
        verify(userRepository, times(1)).existsByDni("12345678A");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userMapper, times(1)).toEntity(validInputDto);
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userMapper, times(1)).toRegisterOutputDto(userEntity);
    }

    @Test
    @DisplayName("Debería lanzar ConflictException cuando el email ya existe")
    void deberiaLanzarExcepcionCuandoEmailYaExiste() {
        // ARRANGE: El email ya existe en la BD
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // ACT & ASSERT: Verificamos que se lanza la excepción correcta
        ConflictException exception = assertThrows(
            ConflictException.class,
            () -> userRegisterUseCase.register(validInputDto),
            "Debería lanzar ConflictException cuando el email existe"
        );

        assertEquals("Email already in use", exception.getMessage());

        // VERIFY: Verificamos que NO se llamó a métodos posteriores
        verify(userRepository, times(1)).existsByEmail("pablo@test.com");
        verify(userRepository, never()).existsByDni(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar ConflictException cuando el DNI ya existe")
    void deberiaLanzarExcepcionCuandoDniYaExiste() {
        // ARRANGE: El email no existe pero el DNI sí
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByDni(anyString())).thenReturn(true);

        // ACT & ASSERT: Verificamos que se lanza la excepción correcta
        ConflictException exception = assertThrows(
            ConflictException.class,
            () -> userRegisterUseCase.register(validInputDto),
            "Debería lanzar ConflictException cuando el DNI existe"
        );

        assertEquals("DNI already in use", exception.getMessage());

        // VERIFY: Verificamos que se validó el email primero
        verify(userRepository, times(1)).existsByEmail("pablo@test.com");
        verify(userRepository, times(1)).existsByDni("12345678A");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería encriptar la contraseña antes de guardar")
    void deberiaEncriptarContrasenaAntesDeGuardar() {
        // ARRANGE
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByDni(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPassword");
        when(userMapper.toEntity(any(UserRegisterInputDto.class))).thenReturn(userEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toRegisterOutputDto(any(UserEntity.class))).thenReturn(expectedOutputDto);

        // ACT
        userRegisterUseCase.register(validInputDto);

        // ASSERT: Verificamos que la contraseña fue encriptada
        verify(passwordEncoder, times(1)).encode("password123");
        
        // IMPORTANTE: El DTO debería tener la contraseña encriptada después del encode
        // Esto ocurre porque el caso de uso modifica el DTO con: dto.setPassword(passwordEncoder.encode(...))
    }

    @Test
    @DisplayName("Debería guardar el usuario en el repositorio")
    void deberiaGuardarUsuarioEnRepositorio() {
        // ARRANGE
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByDni(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(userMapper.toEntity(any(UserRegisterInputDto.class))).thenReturn(userEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toRegisterOutputDto(any(UserEntity.class))).thenReturn(expectedOutputDto);

        // ACT
        userRegisterUseCase.register(validInputDto);

        // ASSERT: Verificamos que se intentó guardar la entidad
        verify(userRepository, times(1)).save(userEntity);
    }
}
