package freelancer.backendtfg.application.port;

import freelancer.backendtfg.infrastructure.controller.dto.input.UserRegisterInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.UserRegisterOutputDto;

public interface UserRegisterUseCase {
    UserRegisterOutputDto register(UserRegisterInputDto dto);
}
