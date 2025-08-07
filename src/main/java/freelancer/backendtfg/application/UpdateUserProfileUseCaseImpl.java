package freelancer.backendtfg.application;

import freelancer.backendtfg.application.port.UpdateUserProfileUseCase;
import freelancer.backendtfg.domain.mapper.UserMapper;
import freelancer.backendtfg.infrastructure.controller.dto.input.UpdateUserProfileInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.UserProfileOutputDto;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUserProfileUseCaseImpl implements UpdateUserProfileUseCase {
    private final UserRepositoryPort userRepository;
    private final UserMapper userMapper;

    @Override
    public UserProfileOutputDto updateProfile(String email, UpdateUserProfileInputDto inputDto) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        user.setName(inputDto.getName());
        user.setSurname(inputDto.getSurname());
        user.setDni(inputDto.getDni());
        UserEntity updatedUser = userRepository.save(user);
        return userMapper.toProfileOutputDto(updatedUser);
    }
} 