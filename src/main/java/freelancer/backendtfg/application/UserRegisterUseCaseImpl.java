package freelancer.backendtfg.application;

import freelancer.backendtfg.application.port.UserRegisterUseCase;
import freelancer.backendtfg.domain.mapper.UserMapper;
import freelancer.backendtfg.infrastructure.repository.UserRepository;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.shared.exceptions.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import freelancer.backendtfg.infrastructure.controller.dto.input.UserRegisterInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.UserRegisterOutputDto;

@Service
@RequiredArgsConstructor
public class UserRegisterUseCaseImpl implements UserRegisterUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserRegisterOutputDto register(UserRegisterInputDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email already in use");
        }

        if (userRepository.existsByDni(dto.getDni())) {
            throw new ConflictException("DNI already in use");
        }

        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        UserEntity user = userMapper.toEntity(dto);
        UserEntity savedUser = userRepository.save(user);

        return userMapper.toRegisterOutputDto(savedUser);
    }
}
