package freelancer.backendtfg.application.port;

import freelancer.backendtfg.infrastructure.controller.dto.input.UpdateUserProfileInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.UserProfileOutputDto;

public interface UpdateUserProfileUseCase {
    UserProfileOutputDto updateProfile(String email, UpdateUserProfileInputDto inputDto);
} 