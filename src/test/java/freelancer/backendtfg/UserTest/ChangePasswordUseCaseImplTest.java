package freelancer.backendtfg.UserTest;

import freelancer.backendtfg.application.useCaseImpl.userUseCaseImpl.ChangePasswordUseCaseImpl;
import freelancer.backendtfg.infrastructure.controller.dto.input.usersInput.ChangePasswordInputDto;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.InvalidCredentialsException;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para ChangePasswordUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear el cambio de contraseña de un usuario
 * - Verificar validación de contraseña actual
 * - Comprobar encriptación de nueva contraseña
 * - Validar manejo de excepciones (usuario no existe, contraseña incorrecta)
 * 
 * FLUJO DE SEGURIDAD:
 * 1. Buscar usuario por email
 * 2. Validar que la contraseña actual es correcta (con BCrypt.matches)
 * 3. Encriptar nueva contraseña (con BCrypt.encode)
 * 4. Guardar usuario con nueva contraseña
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Cambiar Contraseña de Usuario")
class ChangePasswordUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private ChangePasswordUseCaseImpl changePasswordUseCase;

    private String userEmail;
    private ChangePasswordInputDto inputDto;
    private UserEntity userEntity;
    private String oldPasswordEncoded;
    private String newPasswordEncoded;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";
        
        // Contraseñas
        oldPasswordEncoded = "$2a$10$oldEncodedPassword";
        newPasswordEncoded = "$2a$10$newEncodedPassword";

        // DTO con contraseña actual y nueva
        inputDto = new ChangePasswordInputDto();
        inputDto.setOldPassword("oldPassword123");
        inputDto.setNewPassword("newPassword456");

        // Usuario existente con contraseña actual
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Pablo");
        userEntity.setSurname("García");
        userEntity.setEmail("pablo@test.com");
        userEntity.setDni("12345678A");
        userEntity.setPassword(oldPasswordEncoded);
    }

    @Test
    @DisplayName("Debería cambiar la contraseña correctamente cuando la contraseña actual es válida")
    void deberiaCambiarContrasenaCorrectamente() {
        // ARRANGE: Configuramos el comportamiento esperado
        // 1. El usuario existe
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        
        // 2. La contraseña actual coincide
        when(passwordEncoder.matches("oldPassword123", oldPasswordEncoded)).thenReturn(true);
        
        // 3. La nueva contraseña se encripta
        when(passwordEncoder.encode("newPassword456")).thenReturn(newPasswordEncoded);
        
        // 4. El repositorio guarda el usuario
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // ACT: Cambiamos la contraseña
        changePasswordUseCase.changePassword(userEmail, inputDto);

        // ASSERT: Verificamos que la contraseña se actualizó en la entidad
        assertEquals(newPasswordEncoded, userEntity.getPassword(), 
            "La contraseña debería estar actualizada con la nueva encriptada");

        // VERIFY: Verificamos las interacciones
        verify(userRepository, times(1)).findByEmail("pablo@test.com");
        verify(passwordEncoder, times(1)).matches("oldPassword123", oldPasswordEncoded);
        verify(passwordEncoder, times(1)).encode("newPassword456");
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE: El usuario NO existe
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT: Verificamos que se lanza la excepción
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> changePasswordUseCase.changePassword(userEmail, inputDto),
            "Debería lanzar UserNotFoundException cuando el usuario no existe"
        );

        assertEquals("Usuario no encontrado", exception.getMessage());

        // VERIFY: Verificamos que NO se intentó validar ni cambiar contraseña
        verify(userRepository, times(1)).findByEmail("pablo@test.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar InvalidCredentialsException cuando la contraseña actual es incorrecta")
    void deberiaLanzarExcepcionCuandoContrasenaActualIncorrecta() {
        // ARRANGE: El usuario existe pero la contraseña actual no coincide
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // ACT & ASSERT: Verificamos que se lanza la excepción
        InvalidCredentialsException exception = assertThrows(
            InvalidCredentialsException.class,
            () -> changePasswordUseCase.changePassword(userEmail, inputDto),
            "Debería lanzar InvalidCredentialsException con contraseña actual incorrecta"
        );

        assertEquals("La contraseña actual es incorrecta", exception.getMessage());

        // VERIFY: Verificamos que NO se intentó cambiar la contraseña
        verify(userRepository, times(1)).findByEmail("pablo@test.com");
        verify(passwordEncoder, times(1)).matches("oldPassword123", oldPasswordEncoded);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería encriptar la nueva contraseña antes de guardar")
    void deberiaEncriptarNuevaContrasenaAntesDeGuardar() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode("newPassword456")).thenReturn(newPasswordEncoded);
        when(userRepository.save(any())).thenReturn(userEntity);

        // ACT
        changePasswordUseCase.changePassword(userEmail, inputDto);

        // ASSERT: Verificamos que se llamó a encode con la nueva contraseña
        verify(passwordEncoder, times(1)).encode("newPassword456");
        
        // Y que la entidad tiene la contraseña encriptada, NO la plana
        assertEquals(newPasswordEncoded, userEntity.getPassword());
        assertNotEquals("newPassword456", userEntity.getPassword(), 
            "La contraseña NO debería guardarse en texto plano");
    }

    @Test
    @DisplayName("Debería validar contraseña actual usando BCrypt.matches")
    void deberiaValidarContrasenaActualConBCrypt() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("oldPassword123", oldPasswordEncoded)).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn(newPasswordEncoded);
        when(userRepository.save(any())).thenReturn(userEntity);

        // ACT
        changePasswordUseCase.changePassword(userEmail, inputDto);

        // ASSERT: Verificamos que se usó matches para validar
        verify(passwordEncoder, times(1)).matches("oldPassword123", oldPasswordEncoded);
    }

    @Test
    @DisplayName("Debería persistir el usuario con la nueva contraseña")
    void deberiaPersistirUsuarioConNuevaContrasena() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn(newPasswordEncoded);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity savedUser = invocation.getArgument(0);
            assertEquals(newPasswordEncoded, savedUser.getPassword(), 
                "El usuario guardado debería tener la nueva contraseña");
            return savedUser;
        });

        // ACT
        changePasswordUseCase.changePassword(userEmail, inputDto);

        // ASSERT: Verificamos que se guardó el usuario
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Debería mantener el orden correcto de operaciones de seguridad")
    void deberiaManteneerOrdenCorrectoDOperaciones() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn(newPasswordEncoded);
        when(userRepository.save(any())).thenReturn(userEntity);

        // ACT
        changePasswordUseCase.changePassword(userEmail, inputDto);

        // ASSERT: Verificamos el orden con InOrder
        InOrder inOrder = inOrder(userRepository, passwordEncoder);
        inOrder.verify(userRepository).findByEmail(userEmail); // 1. Buscar usuario
        inOrder.verify(passwordEncoder).matches("oldPassword123", oldPasswordEncoded); // 2. Validar old password
        inOrder.verify(passwordEncoder).encode("newPassword456"); // 3. Encriptar new password
        inOrder.verify(userRepository).save(userEntity); // 4. Guardar
    }

    @Test
    @DisplayName("No debería cambiar otros campos del usuario, solo la contraseña")
    void noDeberiaCambiarOtrosCampos() {
        // ARRANGE
        String originalName = userEntity.getName();
        String originalEmail = userEntity.getEmail();
        String originalDni = userEntity.getDni();
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn(newPasswordEncoded);
        when(userRepository.save(any())).thenReturn(userEntity);

        // ACT
        changePasswordUseCase.changePassword(userEmail, inputDto);

        // ASSERT: Verificamos que solo cambió la contraseña
        assertAll("Solo debería cambiar la contraseña",
            () -> assertEquals(originalName, userEntity.getName(), "Name no debe cambiar"),
            () -> assertEquals(originalEmail, userEntity.getEmail(), "Email no debe cambiar"),
            () -> assertEquals(originalDni, userEntity.getDni(), "DNI no debe cambiar"),
            () -> assertEquals(newPasswordEncoded, userEntity.getPassword(), "Password debe cambiar")
        );
    }
}
