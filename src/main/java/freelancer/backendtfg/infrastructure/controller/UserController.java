package freelancer.backendtfg.infrastructure.controller;

import freelancer.backendtfg.application.port.ChangePasswordUseCase;
import freelancer.backendtfg.application.port.DeleteUserUseCase;
import freelancer.backendtfg.application.port.GetUserProfileUseCase;
import freelancer.backendtfg.application.port.UserLoginUseCase;
import freelancer.backendtfg.application.port.UserRegisterUseCase;
import freelancer.backendtfg.application.port.UpdateUserProfileUseCase;
import freelancer.backendtfg.infrastructure.controller.dto.input.ChangePasswordInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.input.UserLoginInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.input.UserRegisterInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.input.UpdateUserProfileInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.UserLoginOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.UserProfileOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.UserRegisterOutputDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserLoginUseCase userLoginUseCase;
    private final UserRegisterUseCase userRegisterUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    @PostMapping("/login")
    public ResponseEntity<UserLoginOutputDto> login(@Valid @RequestBody UserLoginInputDto loginDto) {
        UserLoginOutputDto user = userLoginUseCase.login(loginDto);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterOutputDto> register(@Valid @RequestBody UserRegisterInputDto registerDto) {
        UserRegisterOutputDto user = userRegisterUseCase.register(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileOutputDto> getCurrentUser(@AuthenticationPrincipal String email) {
        UserProfileOutputDto user = getUserProfileUseCase.getProfileByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<UserProfileOutputDto> updateCurrentUser(@AuthenticationPrincipal String email, @Valid @RequestBody UpdateUserProfileInputDto inputDto) {
        UserProfileOutputDto updatedUser = updateUserProfileUseCase.updateProfile(email, inputDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal String email, @Valid @RequestBody ChangePasswordInputDto inputDto) {
        changePasswordUseCase.changePassword(email, inputDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCurrentUser(@AuthenticationPrincipal String email) {
        deleteUserUseCase.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }
}
