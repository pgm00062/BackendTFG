package freelancer.backendtfg.application.port;

import freelancer.backendtfg.infrastructure.controller.dto.input.ChangePasswordInputDto;

public interface ChangePasswordUseCase {
    void changePassword(String email, ChangePasswordInputDto inputDto);
} 