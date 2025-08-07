package freelancer.backendtfg.application;

import freelancer.backendtfg.application.port.UserLoginUseCase;
import freelancer.backendtfg.domain.mapper.UserMapper;
import freelancer.backendtfg.infrastructure.controller.dto.input.UserLoginInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.UserLoginOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.InvalidCredentialsException;
import freelancer.backendtfg.shared.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginUseCaseImpl implements UserLoginUseCase {

    private final UserRepositoryPort userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public UserLoginOutputDto login(UserLoginInputDto userLoginInputDto) {
        UserEntity user = userRepository.findByEmail(userLoginInputDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(userLoginInputDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Contraseña incorrecta");
        }

        // ⬅️ Generar token con el email del usuario (podemos incluir más info posteriormente)
        String token = jwtUtils.generateToken(user.getEmail());

        return new UserLoginOutputDto(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                token
        );
    }

}
