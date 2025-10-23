package freelancer.backendtfg.UserTest;

import freelancer.backendtfg.application.useCaseImpl.userUseCaseImpl.DeleteUserUseCaseImpl;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para DeleteUserUseCaseImpl
 * 
 * PROPÓSITO:
 * - Testear la eliminación de un usuario por email
 * - Verificar que primero se busca el usuario (para validar existencia)
 * - Comprobar que se elimina correctamente del repositorio
 * - Validar manejo de excepciones cuando el usuario no existe
 * 
 * PATRÓN:
 * - Caso de uso simple pero crítico (operación destructiva)
 * - Importante verificar que NO se elimina si el usuario no existe
 * - No tiene valor de retorno (void)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Eliminar Usuario")
class DeleteUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private DeleteUserUseCaseImpl deleteUserUseCase;

    private String userEmail;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";

        // Usuario a eliminar
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Pablo");
        userEntity.setSurname("García");
        userEntity.setEmail("pablo@test.com");
        userEntity.setDni("12345678A");
        userEntity.setPassword("$2a$10$encodedPassword");
    }

    @Test
    @DisplayName("Debería eliminar el usuario correctamente cuando existe")
    void deberiaEliminarUsuarioCorrectamente() {
        // ARRANGE: El usuario existe
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        doNothing().when(userRepository).delete(any(UserEntity.class));

        // ACT: Eliminamos el usuario
        // NOTA: Este método no retorna nada (void)
        assertDoesNotThrow(() -> deleteUserUseCase.deleteByEmail(userEmail),
            "No debería lanzar excepción al eliminar usuario existente");

        // VERIFY: Verificamos las interacciones
        verify(userRepository, times(1)).findByEmail("pablo@test.com");
        verify(userRepository, times(1)).delete(userEntity);
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE: El usuario NO existe
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT: Verificamos que se lanza la excepción
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> deleteUserUseCase.deleteByEmail(userEmail),
            "Debería lanzar UserNotFoundException cuando el usuario no existe"
        );

        assertEquals("Usuario no encontrado", exception.getMessage());

        // VERIFY: Verificamos que NO se intentó eliminar
        verify(userRepository, times(1)).findByEmail("pablo@test.com");
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debería eliminar exactamente el usuario encontrado")
    void deberiaEliminarExactamenteElUsuarioEncontrado() {
        // ARRANGE
        UserEntity specificUser = new UserEntity();
        specificUser.setId(999L);
        specificUser.setEmail("specific@test.com");
        
        when(userRepository.findByEmail("specific@test.com")).thenReturn(Optional.of(specificUser));
        doNothing().when(userRepository).delete(any());

        // ACT
        deleteUserUseCase.deleteByEmail("specific@test.com");

        // ASSERT: Verificamos que se eliminó exactamente ese usuario
        verify(userRepository, times(1)).delete(specificUser);
    }

    @Test
    @DisplayName("Debería buscar usuario por email antes de eliminar")
    void deberiaBuscarPorEmailAntesDeEliminar() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        doNothing().when(userRepository).delete(any());

        // ACT
        deleteUserUseCase.deleteByEmail(userEmail);

        // ASSERT: Verificamos que primero se buscó el usuario
        verify(userRepository, times(1)).findByEmail(userEmail);
    }

    @Test
    @DisplayName("Debería respetar el orden: buscar primero, luego eliminar")
    void deberiaRespetarOrdenDeBuscarLuegoEliminar() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        doNothing().when(userRepository).delete(any());

        // ACT
        deleteUserUseCase.deleteByEmail(userEmail);

        // ASSERT: Verificamos el orden con InOrder
        InOrder inOrder = inOrder(userRepository);
        inOrder.verify(userRepository).findByEmail(userEmail); // 1. Buscar
        inOrder.verify(userRepository).delete(userEntity); // 2. Eliminar
    }

    @Test
    @DisplayName("No debería ejecutar delete si el usuario no existe")
    void noDeberiaEjecutarDeleteSiUsuarioNoExiste() {
        // ARRANGE: Usuario no existe
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT: Intentamos eliminar (lanzará excepción)
        try {
            deleteUserUseCase.deleteByEmail(userEmail);
        } catch (UserNotFoundException e) {
            // Excepción esperada, la ignoramos
        }

        // ASSERT: Verificamos que NUNCA se llamó a delete
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debería llamar a delete una sola vez")
    void deberiaLlamarADeleteUnaSolaVez() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        doNothing().when(userRepository).delete(any());

        // ACT
        deleteUserUseCase.deleteByEmail(userEmail);

        // ASSERT: Verificamos que delete se llamó exactamente una vez
        verify(userRepository, times(1)).delete(userEntity);
    }

    @Test
    @DisplayName("Debería funcionar con diferentes emails")
    void deberiaFuncionarConDiferentesEmails() {
        // ARRANGE: Creamos varios usuarios con diferentes emails
        String email1 = "user1@test.com";
        String email2 = "user2@test.com";
        
        UserEntity user1 = new UserEntity();
        user1.setEmail(email1);
        
        UserEntity user2 = new UserEntity();
        user2.setEmail(email2);
        
        when(userRepository.findByEmail(email1)).thenReturn(Optional.of(user1));
        when(userRepository.findByEmail(email2)).thenReturn(Optional.of(user2));
        doNothing().when(userRepository).delete(any());

        // ACT: Eliminamos ambos usuarios
        deleteUserUseCase.deleteByEmail(email1);
        deleteUserUseCase.deleteByEmail(email2);

        // ASSERT: Verificamos que se eliminaron los usuarios correctos
        verify(userRepository, times(1)).findByEmail(email1);
        verify(userRepository, times(1)).findByEmail(email2);
        verify(userRepository, times(1)).delete(user1);
        verify(userRepository, times(1)).delete(user2);
    }
}
