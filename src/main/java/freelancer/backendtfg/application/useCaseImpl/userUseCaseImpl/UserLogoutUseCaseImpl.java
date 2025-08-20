package freelancer.backendtfg.application.useCaseImpl.userUseCaseImpl;

import freelancer.backendtfg.application.port.userUseCasePort.UserLogoutUseCase;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLogoutUseCaseImpl implements UserLogoutUseCase {

    private final UserRepositoryPort userRepository;

    public void logout(String userEmail) {
        // Validar que el usuario existe
        userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        // Logout simple: no se almacena nada, el frontend debe eliminar el token.
    }
}