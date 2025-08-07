package freelancer.backendtfg.infrastructure.controller;

import freelancer.backendtfg.application.port.CreateProjectUseCase;
import freelancer.backendtfg.application.port.UpdateProjectUseCase;
import freelancer.backendtfg.application.port.DeleteProjectUseCase;
import freelancer.backendtfg.application.port.ListUserProjectsUseCase;
import freelancer.backendtfg.application.port.SearchUserProjectsByNameUseCase;
import freelancer.backendtfg.application.port.UpdateProjectStatusUseCase;
import freelancer.backendtfg.infrastructure.controller.dto.input.ProjectCreateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.ProjectOutputDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import freelancer.backendtfg.infrastructure.controller.dto.input.ProjectStatusUpdateInputDto;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final CreateProjectUseCase createProjectUseCase;
    private final UpdateProjectUseCase updateProjectUseCase;
    private final DeleteProjectUseCase deleteProjectUseCase;
    private final ListUserProjectsUseCase listUserProjectsUseCase;
    private final SearchUserProjectsByNameUseCase searchUserProjectsByNameUseCase;
    private final UpdateProjectStatusUseCase updateProjectStatusUseCase;

    @PostMapping("/create")
    public ResponseEntity<ProjectOutputDto> createProject(@AuthenticationPrincipal String email,
                                                         @Valid @RequestBody ProjectCreateInputDto inputDto) {
        ProjectOutputDto project = createProjectUseCase.createProject(email, inputDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProjectOutputDto> updateProject(@PathVariable Long id,
                                                         @AuthenticationPrincipal String email,
                                                         @Valid @RequestBody ProjectCreateInputDto inputDto) {
        ProjectOutputDto updated = updateProjectUseCase.updateProject(id, email, inputDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id, @AuthenticationPrincipal String email) {
        deleteProjectUseCase.deleteProject(id, email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<ProjectOutputDto> updateProjectStatus(@PathVariable Long id,
                                                               @AuthenticationPrincipal String email,
                                                               @Valid @RequestBody ProjectStatusUpdateInputDto inputDto) {
        ProjectOutputDto updated = updateProjectStatusUseCase.updateStatus(id, email, inputDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<ProjectOutputDto>> listProjects(@AuthenticationPrincipal String email,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Page<ProjectOutputDto> projects = listUserProjectsUseCase.listProjects(email, PageRequest.of(page, size));
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/searchName")
    public ResponseEntity<Page<ProjectOutputDto>> searchProjects(@AuthenticationPrincipal String email,
                                                                @RequestParam String name,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        Page<ProjectOutputDto> projects = searchUserProjectsByNameUseCase.searchProjects(email, name, PageRequest.of(page, size));
        return ResponseEntity.ok(projects);
    }
}