package freelancer.backendtfg.infrastructure.controller;

import freelancer.backendtfg.application.port.userUseCasePort.ChangePasswordUseCase;
import freelancer.backendtfg.application.port.userUseCasePort.DeleteUserUseCase;
import freelancer.backendtfg.application.port.userUseCasePort.GetUserProfileUseCase;
import freelancer.backendtfg.application.port.userUseCasePort.UserLoginUseCase;
import freelancer.backendtfg.application.port.userUseCasePort.UserRegisterUseCase;
import freelancer.backendtfg.application.port.userUseCasePort.UpdateUserProfileUseCase;
import freelancer.backendtfg.infrastructure.controller.dto.input.usersInput.ChangePasswordInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.input.usersInput.UserLoginInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.input.usersInput.UserRegisterInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.input.usersInput.UpdateUserProfileInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput.UserLoginOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput.UserProfileOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput.UserRegisterOutputDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @ApiOperation(value = "Login de usuario", notes= "EL usuario hace login a partir del email y la contraseña. Se" +
            "genera un token de autenticación para el posterior uso de los demás endpoints")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operación exitosa"),
            @ApiResponse(code = 400, message = "Datos de entrada no válidos"),
            @ApiResponse(code = 401, message = "Credencias invalidos"),
            @ApiResponse(code = 404, message = "Usuario no encontrado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<UserLoginOutputDto> login(@Valid @RequestBody UserLoginInputDto loginDto) {
        UserLoginOutputDto user = userLoginUseCase.login(loginDto);
        return ResponseEntity.ok(user);
    }

    @ApiOperation(value="Registro del usuario",  notes= "El usuario realiza el registro, introduciendo todos los datos personales")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Operación exitosa"),
            @ApiResponse(code = 400, message = "Datos de entrada no válidos"),
            @ApiResponse(code = 409, message = "El email ya existe registrado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @PostMapping("/register")
    public ResponseEntity<UserRegisterOutputDto> register(@Valid @RequestBody UserRegisterInputDto registerDto) {
        UserRegisterOutputDto user = userRegisterUseCase.register(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @ApiOperation(value="Obtener un usuario", notes="Obtener un usuario a partir de su email")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operación exitosa"),
            @ApiResponse(code = 401, message = "No tiene permisos para realizar la acción"),
            @ApiResponse(code = 404, message = "Usuario no encontrado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileOutputDto> getCurrentUser(@AuthenticationPrincipal String email) {
        UserProfileOutputDto user = getUserProfileUseCase.getProfileByEmail(email);
        return ResponseEntity.ok(user);
    }

    @ApiOperation(value = "Actulizar datos de usuario", notes = "A partir de su email, se podrá actualizar los datos del usuario")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operación exitosa"),
            @ApiResponse(code = 401,  message = "No tiene permisos para realizar la acción"),
            @ApiResponse(code = 404,  message = "Usuario no encontrado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @PutMapping("/update")
    public ResponseEntity<UserProfileOutputDto> updateCurrentUser(@AuthenticationPrincipal String email, @Valid @RequestBody UpdateUserProfileInputDto inputDto) {
        UserProfileOutputDto updatedUser = updateUserProfileUseCase.updateProfile(email, inputDto);
        return ResponseEntity.ok(updatedUser);
    }

    @ApiOperation(value = "Cambiar contraseña", notes = "Cambiar la contraseña del usuario")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message= "Operación exitosa"),
            @ApiResponse(code = 401,  message = "No tiene permisos para realizar la acción"),
            @ApiResponse(code = 404,  message = "Usuario no encontrado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal String email, @Valid @RequestBody ChangePasswordInputDto inputDto) {
        changePasswordUseCase.changePassword(email, inputDto);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Eliminar un usuario", notes = "Eliminar un cliente a partir de su email")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message= "Operación exitosa"),
            @ApiResponse(code = 401,  message = "No tiene permisos para realizar la acción"),
            @ApiResponse(code = 404,  message = "Usuario no encontrado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCurrentUser(@AuthenticationPrincipal String email) {
        deleteUserUseCase.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }
}