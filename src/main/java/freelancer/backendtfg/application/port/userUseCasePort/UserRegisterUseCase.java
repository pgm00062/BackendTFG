package freelancer.backendtfg.application.port.userUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.input.usersInput.UserRegisterInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput.UserRegisterOutputDto;

public interface UserRegisterUseCase {
    UserRegisterOutputDto register(UserRegisterInputDto dto);
}
