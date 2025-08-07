package freelancer.backendtfg.infrastructure.controller;

import freelancer.backendtfg.application.port.StartTimeSessionUseCase;
import freelancer.backendtfg.application.port.EndTimeSessionUseCase;
import freelancer.backendtfg.application.port.ListUserTimeSessionsUseCase;
import freelancer.backendtfg.application.port.GetActiveTimeSessionUseCase;
import freelancer.backendtfg.application.port.GetProjectTotalTimeUseCase;
import freelancer.backendtfg.infrastructure.controller.dto.input.TimeStartInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.input.TimeEndInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.TimeSessionOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.ProjectTotalTimeOutputDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/start")
    public ResponseEntity<TimeSessionOutputDto> startSession(@AuthenticationPrincipal String email,
                                                           @Valid @RequestBody TimeStartInputDto inputDto) {
        TimeSessionOutputDto timeSession = startTimeSessionUseCase.startSession(email, inputDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(timeSession);
    }

    @PostMapping("/end")
    public ResponseEntity<TimeSessionOutputDto> endSession(@AuthenticationPrincipal String email,
                                                         @Valid @RequestBody TimeEndInputDto inputDto) {
        TimeSessionOutputDto timeSession = endTimeSessionUseCase.endSession(email, inputDto);
        return ResponseEntity.ok(timeSession);
    }

    @GetMapping("/active")
    public ResponseEntity<TimeSessionOutputDto> getActiveSession(@AuthenticationPrincipal String email) {
        TimeSessionOutputDto activeSession = getActiveTimeSessionUseCase.getActiveSession(email);
        return ResponseEntity.ok(activeSession);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<TimeSessionOutputDto>> listTimeSessions(@AuthenticationPrincipal String email,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        Page<TimeSessionOutputDto> timeSessions = listUserTimeSessionsUseCase.listTimeSessions(email, PageRequest.of(page, size));
        return ResponseEntity.ok(timeSessions);
    }

    @GetMapping("/project/{projectId}/total")
    public ResponseEntity<ProjectTotalTimeOutputDto> getProjectTotalTime(@AuthenticationPrincipal String email,
                                                                       @PathVariable Long projectId) {
        ProjectTotalTimeOutputDto totalTime = getProjectTotalTimeUseCase.getProjectTotalTime(email, projectId);
        return ResponseEntity.ok(totalTime);
    }
} 