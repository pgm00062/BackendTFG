package freelancer.backendtfg.application.useCaseImpl.userUseCaseImpl;

import freelancer.backendtfg.application.port.userUseCasePort.GetUserProfileUseCase;
import freelancer.backendtfg.domain.mapper.UserMapper;
import freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput.UserProfileOutputDto;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserProfileUseCaseImpl implements GetUserProfileUseCase {
    private final UserRepositoryPort userRepository;
    private final UserMapper userMapper;

    @Override
    public UserProfileOutputDto getProfileByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        return userMapper.toProfileOutputDto(user);
    }
} 