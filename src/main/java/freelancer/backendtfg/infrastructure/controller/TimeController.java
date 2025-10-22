package freelancer.backendtfg.infrastructure.controller;

import freelancer.backendtfg.application.port.timeUseCasePort.StartTimeSessionUseCase;
import freelancer.backendtfg.application.port.timeUseCasePort.EndTimeSessionUseCase;
import freelancer.backendtfg.application.port.timeUseCasePort.ListUserTimeSessionsUseCase;
import freelancer.backendtfg.application.port.timeUseCasePort.PauseResumeTimeSessionUseCase;
import freelancer.backendtfg.application.port.timeUseCasePort.GetActiveTimeSessionUseCase;
import freelancer.backendtfg.application.port.timeUseCasePort.GetProjectTotalTimeUseCase;
import freelancer.backendtfg.infrastructure.controller.dto.input.timesInput.TimeStartInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.input.timesInput.TimeEndInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionDailyOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectTotalTimeOutputDto;
import freelancer.backendtfg.application.port.timeUseCasePort.GetStatiticsTimeUseCase;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@RestController
@RequestMapping("/times")
@RequiredArgsConstructor
public class TimeController {

    private final StartTimeSessionUseCase startTimeSessionUseCase;
    private final EndTimeSessionUseCase endTimeSessionUseCase;
    private final ListUserTimeSessionsUseCase listUserTimeSessionsUseCase;
    private final GetActiveTimeSessionUseCase getActiveTimeSessionUseCase;
    private final GetProjectTotalTimeUseCase getProjectTotalTimeUseCase;
    private final PauseResumeTimeSessionUseCase pauseResumeTimeSessionUseCase;
    private final GetStatiticsTimeUseCase getStatiticsTimeUseCase;

    @ApiOperation(value = "Iniciar sesión de tiempo", notes = "Inicia una nueva sesión de tiempo para el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Sesión iniciada correctamente"),
            @ApiResponse(code = 400, message = "Datos de entrada no válidos"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @PostMapping("/start")
    public ResponseEntity<TimeSessionOutputDto> startSession(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody TimeStartInputDto inputDto) {
        TimeSessionOutputDto timeSession = startTimeSessionUseCase.startSession(email, inputDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(timeSession);
    }

    @ApiOperation(value = "Finalizar sesión de tiempo", notes = "Finaliza una sesión de tiempo activa para el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sesión finalizada correctamente"),
            @ApiResponse(code = 400, message = "Datos de entrada no válidos"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 404, message = "Sesión activa no encontrada"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @PostMapping("/end")
    public ResponseEntity<TimeSessionOutputDto> endSession(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody TimeEndInputDto inputDto) {
        TimeSessionOutputDto timeSession = endTimeSessionUseCase.endSession(email, inputDto);
        return ResponseEntity.ok(timeSession);
    }

    @ApiOperation(value = "Obtener sesión activa", notes = "Obtiene la sesión de tiempo activa para el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sesión activa obtenida"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 404, message = "No hay sesión activa"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @GetMapping("/active")
    public ResponseEntity<TimeSessionOutputDto> getActiveSession(@AuthenticationPrincipal String email) {
        TimeSessionOutputDto activeSession = getActiveTimeSessionUseCase.getActiveSession(email);
        return ResponseEntity.ok(activeSession);
    }

    @ApiOperation(value = "Listar sesiones de tiempo", notes = "Obtiene una lista paginada de sesiones de tiempo del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Listado de sesiones obtenido"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @GetMapping("/list")
    public ResponseEntity<Page<TimeSessionOutputDto>> listTimeSessions(
            @AuthenticationPrincipal String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TimeSessionOutputDto> timeSessions = listUserTimeSessionsUseCase.listTimeSessions(email, PageRequest.of(page, size));
        return ResponseEntity.ok(timeSessions);
    }

    @ApiOperation(value = "Tiempo total por proyecto", notes = "Obtiene el tiempo total registrado para un proyecto específico del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tiempo total obtenido"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 404, message = "Proyecto no encontrado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @GetMapping("/project/{projectId}/total")
    public ResponseEntity<ProjectTotalTimeOutputDto> getProjectTotalTime(
            @AuthenticationPrincipal String email,
            @PathVariable Long projectId) {
        ProjectTotalTimeOutputDto totalTime = getProjectTotalTimeUseCase.getProjectTotalTime(email, projectId);
        return ResponseEntity.ok(totalTime);
    }

    @ApiOperation(value = "Pausar sesión de tiempo", notes = "Pausa la sesión de tiempo activa para el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sesión pausada correctamente"),
            @ApiResponse(code = 400, message = "Sesión ya pausada"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 404, message = "No hay sesión activa"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @PostMapping("/pause")
    public ResponseEntity<TimeSessionOutputDto> pauseSession(@AuthenticationPrincipal String email) {
        TimeSessionOutputDto pausedSession = pauseResumeTimeSessionUseCase.pauseSession(email);
        return ResponseEntity.ok(pausedSession);
    }

    @ApiOperation(value = "Reanudar sesión de tiempo", notes = "Reanuda la sesión de tiempo pausada para el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sesión reanudada correctamente"),
            @ApiResponse(code = 400, message = "Sesión no está pausada"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 404, message = "No hay sesión activa"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @PostMapping("/resume")
    public ResponseEntity<TimeSessionOutputDto> resumeSession(@AuthenticationPrincipal String email) {
        TimeSessionOutputDto resumedSession = pauseResumeTimeSessionUseCase.resumeSession(email);
        return ResponseEntity.ok(resumedSession);
    }

    @ApiOperation(value = "Tiempo total diario", notes = "Obtiene el tiempo total trabajado en un día específico para el usuario autenticado, sumando todas las sesiones finalizadas.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Tiempo total obtenido"),
        @ApiResponse(code = 400, message = "Fecha inválida"),
        @ApiResponse(code = 401, message = "No autorizado"),
        @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @GetMapping("/total/day")
        public ResponseEntity<TimeSessionDailyOutputDto> getDailyTotalTime(
            @AuthenticationPrincipal String email,
            @RequestParam String date) {  // Formato esperado: YYYY-MM-DD

        // Parsear la fecha
        LocalDate localDate = LocalDate.parse(date);

        // Calcular el rango del día
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.plusDays(1).atStartOfDay();

        // Llamar al servicio con el rango
        TimeSessionDailyOutputDto totalTime =
                getProjectTotalTimeUseCase.getDailyTotalTime(email, startOfDay, endOfDay);

        return ResponseEntity.ok(totalTime);
    }

    @ApiOperation(value = "Tiempo total en el último mes", notes = "Obtiene el tiempo total trabajado en el último mes para el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tiempo total obtenido"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @GetMapping("/total/last-month")
    public ResponseEntity<TimeSessionDailyOutputDto> getTotalTimeLastMonth(@AuthenticationPrincipal String email) {
        TimeSessionDailyOutputDto totalTime = getStatiticsTimeUseCase.getTotalTimeLastMonth(email);
        return ResponseEntity.ok(totalTime);
    }

    @ApiOperation(value = "Tiempo total en el año actual", notes = "Obtiene el tiempo total trabajado en el año actual para el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tiempo total obtenido"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @GetMapping("/total/this-year")
    public ResponseEntity<TimeSessionDailyOutputDto> getTotalTimeThisYear(@AuthenticationPrincipal String email) {
        TimeSessionDailyOutputDto totalTime = getStatiticsTimeUseCase.getTotalTimeThisYear(email);
        return ResponseEntity.ok(totalTime);
    }
}