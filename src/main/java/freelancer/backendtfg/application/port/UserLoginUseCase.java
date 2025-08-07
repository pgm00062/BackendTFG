package freelancer.backendtfg.application.port;

import freelancer.backendtfg.infrastructure.controller.dto.input.UserLoginInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.UserLoginOutputDto;

public interface UserLoginUseCase {
    UserLoginOutputDto login(UserLoginInputDto userLoginInputDto);
}
