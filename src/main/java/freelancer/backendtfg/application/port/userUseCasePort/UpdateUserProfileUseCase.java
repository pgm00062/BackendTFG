package freelancer.backendtfg.application.port.userUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.input.usersInput.UpdateUserProfileInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput.UserProfileOutputDto;

public interface UpdateUserProfileUseCase {
    UserProfileOutputDto updateProfile(String email, UpdateUserProfileInputDto inputDto);
} 