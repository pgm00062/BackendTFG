package freelancer.backendtfg.application.port.userUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.input.usersInput.ChangePasswordInputDto;

public interface ChangePasswordUseCase {
    void changePassword(String email, ChangePasswordInputDto inputDto);
} 