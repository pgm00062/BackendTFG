package freelancer.backendtfg.application.port.userUseCasePort;

import freelancer.backendtfg.infrastructure.controller.dto.input.usersInput.UserLoginInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput.UserLoginOutputDto;

public interface UserLoginUseCase {
    UserLoginOutputDto login(UserLoginInputDto userLoginInputDto);
}
