package freelancer.backendtfg.UserTest;

import freelancer.backendtfg.application.useCaseImpl.userUseCaseImpl.UserLogoutUseCaseImpl;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para UserLogoutUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear la lógica de logout (cierre de sesión)
 * - Verificar que se valida la existencia del usuario
 * - Comprobar manejo de excepciones cuando el usuario no existe
 * 
 * NOTA IMPORTANTE SOBRE EL LOGOUT:
 * - Este caso de uso es STATELESS (sin estado en servidor)
 * - Solo valida que el usuario existe, no almacena nada
 * - La responsabilidad de eliminar el token recae en el FRONTEND
 * - Es un patrón común en aplicaciones JWT donde el token no se almacena en servidor
 * 
 * COMPORTAMIENTO:
 * - Si el usuario existe: operación exitosa (no hace nada más)
 * - Si el usuario NO existe: lanza RuntimeException
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Logout de Usuario")
class UserLogoutUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private UserLogoutUseCaseImpl userLogoutUseCase;

    private String userEmail;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";

        // Usuario existente
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Pablo");
        userEntity.setSurname("García");
        userEntity.setEmail("pablo@test.com");
        userEntity.setDni("12345678A");
        userEntity.setPassword("$2a$10$encodedPassword");
    }

    @Test
    @DisplayName("Debería ejecutar logout correctamente cuando el usuario existe")
    void deberiaEjecutarLogoutCorrectamente() {
        // ARRANGE: El usuario existe
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));

        // ACT: Ejecutamos el logout
        // NOTA: Este método no retorna nada (void) y no lanza excepción si el usuario existe
        assertDoesNotThrow(() -> userLogoutUseCase.logout(userEmail),
            "No debería lanzar excepción al hacer logout de usuario existente");

        // VERIFY: Verificamos que se buscó el usuario
        verify(userRepository, times(1)).findByEmail("pablo@test.com");
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE: El usuario NO existe
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT: Verificamos que se lanza la excepción
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> userLogoutUseCase.logout(userEmail),
            "Debería lanzar RuntimeException cuando el usuario no existe"
        );

        assertEquals("Usuario no encontrado", exception.getMessage());

        // VERIFY: Verificamos que se intentó buscar el usuario
        verify(userRepository, times(1)).findByEmail("pablo@test.com");
    }

    @Test
    @DisplayName("Debería validar la existencia del usuario antes de hacer logout")
    void deberiaValidarExistenciaDelUsuario() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));

        // ACT
        userLogoutUseCase.logout(userEmail);

        // ASSERT: Verificamos que se llamó a findByEmail para validar
        verify(userRepository, times(1)).findByEmail(userEmail);
    }

    @Test
    @DisplayName("No debería modificar ni eliminar el usuario en la BD")
    void noDeberiaModificarNiEliminarUsuario() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));

        // ACT
        userLogoutUseCase.logout(userEmail);

        // ASSERT: Verificamos que NO se llamó a save ni delete
        // Solo se valida la existencia, no se modifica nada
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debería buscar usuario por el email proporcionado")
    void deberiaBuscarPorEmailCorrecto() {
        // ARRANGE
        String customEmail = "custom@test.com";
        UserEntity customUser = new UserEntity();
        customUser.setEmail(customEmail);
        
        when(userRepository.findByEmail(customEmail)).thenReturn(Optional.of(customUser));

        // ACT
        userLogoutUseCase.logout(customEmail);

        // ASSERT: Verificamos que se buscó por el email correcto
        verify(userRepository, times(1)).findByEmail(customEmail);
    }

    @Test
    @DisplayName("Debería funcionar con múltiples logouts del mismo usuario")
    void deberiaFuncionarConMultiplesLogouts() {
        // ARRANGE: El usuario existe
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));

        // ACT: Hacemos logout varias veces
        assertDoesNotThrow(() -> {
            userLogoutUseCase.logout(userEmail);
            userLogoutUseCase.logout(userEmail);
            userLogoutUseCase.logout(userEmail);
        });

        // ASSERT: Verificamos que se buscó el usuario cada vez
        verify(userRepository, times(3)).findByEmail("pablo@test.com");
    }

    @Test
    @DisplayName("Debería ser idempotente (puede ejecutarse múltiples veces sin efectos secundarios)")
    void deberiaSerIdempotente() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));

        // ACT: Ejecutamos logout múltiples veces
        userLogoutUseCase.logout(userEmail);
        userLogoutUseCase.logout(userEmail);

        // ASSERT: No hay efectos secundarios, solo validación
        // Esto es correcto porque el logout es stateless
        verify(userRepository, times(2)).findByEmail(userEmail);
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debería delegar la eliminación del token al frontend")
    void deberiaDelegarEliminacionTokenAlFrontend() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));

        // ACT
        userLogoutUseCase.logout(userEmail);

        // ASSERT: Verificamos que NO hay lógica de invalidación de tokens en el backend
        // Este test documenta que el diseño es stateless:
        // - No se almacenan tokens en BD
        // - No hay lista negra de tokens
        // - El frontend debe eliminar el token del localStorage/sessionStorage
        verify(userRepository, only()).findByEmail(anyString());
    }

    /**
     * NOTA PEDAGÓGICA:
     * 
     * Este caso de uso implementa un patrón STATELESS LOGOUT común en aplicaciones JWT:
     * 
     * ¿Por qué solo valida la existencia del usuario?
     * - Los tokens JWT son autónomos (contienen toda la info necesaria)
     * - No se almacenan en el servidor (stateless)
     * - El logout se hace eliminando el token del cliente (navegador)
     * 
     * Ventajas:
     * - Escalabilidad: No hay estado en servidor
     * - Simplicidad: No requiere BD para gestionar sesiones
     * 
     * Desventajas:
     * - No se pueden invalidar tokens antes de su expiración
     * - Si necesitas revocación inmediata, necesitarías una "blacklist" de tokens
     * 
     * Para una revocación más estricta, podrías:
     * 1. Usar refresh tokens almacenados en BD
     * 2. Implementar una blacklist de tokens
     * 3. Usar tiempos de expiración cortos
     */
}
