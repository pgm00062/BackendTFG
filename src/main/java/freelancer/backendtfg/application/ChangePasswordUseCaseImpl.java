package freelancer.backendtfg.application;

import freelancer.backendtfg.application.port.ChangePasswordUseCase;
import freelancer.backendtfg.infrastructure.controller.dto.input.ChangePasswordInputDto;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.shared.exceptions.InvalidCredentialsException;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangePasswordUseCaseImpl implements ChangePasswordUseCase {
    private final UserRepositoryPort userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void changePassword(String email, ChangePasswordInputDto inputDto) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        if (!passwordEncoder.matches(inputDto.getOldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("La contraseña actual es incorrecta");
        }
        user.setPassword(passwordEncoder.encode(inputDto.getNewPassword()));
        userRepository.save(user);
    }
} 