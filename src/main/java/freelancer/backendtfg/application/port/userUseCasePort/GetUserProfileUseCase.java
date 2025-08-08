package freelancer.backendtfg.application.port.userUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput.UserProfileOutputDto;

public interface GetUserProfileUseCase {
    UserProfileOutputDto getProfileByEmail(String email);
} 