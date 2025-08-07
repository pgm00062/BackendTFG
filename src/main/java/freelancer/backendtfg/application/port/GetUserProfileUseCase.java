package freelancer.backendtfg.application.port;

import freelancer.backendtfg.infrastructure.controller.dto.output.UserProfileOutputDto;

public interface GetUserProfileUseCase {
    UserProfileOutputDto getProfileByEmail(String email);
} 