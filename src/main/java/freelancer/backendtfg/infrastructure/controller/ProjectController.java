package freelancer.backendtfg.infrastructure.controller;

import freelancer.backendtfg.application.port.projectUseCasePort.CreateProjectUseCase;
import freelancer.backendtfg.application.port.projectUseCasePort.UpdateProjectUseCase;
import freelancer.backendtfg.application.port.projectUseCasePort.DeleteProjectUseCase;
import freelancer.backendtfg.application.port.projectUseCasePort.ListUserProjectsUseCase;
import freelancer.backendtfg.application.port.projectUseCasePort.SearchUserProjectsByNameUseCase;
import freelancer.backendtfg.application.port.projectUseCasePort.UpdateProjectStatusUseCase;
import freelancer.backendtfg.infrastructure.controller.dto.input.projectsInput.ProjectCreateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import freelancer.backendtfg.infrastructure.controller.dto.input.projectsInput.ProjectStatusUpdateInputDto;

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

    @ApiOperation(value = "Crear proyecto", notes = "Crea un nuevo proyecto para el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Proyecto creado exitosamente"),
            @ApiResponse(code = 400, message = "Datos de entrada no válidos"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @PostMapping("/create")
    public ResponseEntity<ProjectOutputDto> createProject(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody ProjectCreateInputDto inputDto) {
        ProjectOutputDto project = createProjectUseCase.createProject(email, inputDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }

    @ApiOperation(value = "Actualizar proyecto", notes = "Actualiza los datos de un proyecto existente por su ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Proyecto actualizado exitosamente"),
            @ApiResponse(code = 400, message = "Datos de entrada no válidos"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 404, message = "Proyecto no encontrado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<ProjectOutputDto> updateProject(
            @PathVariable Long id,
            @AuthenticationPrincipal String email,
            @Valid @RequestBody ProjectCreateInputDto inputDto) {
        ProjectOutputDto updated = updateProjectUseCase.updateProject(id, email, inputDto);
        return ResponseEntity.ok(updated);
    }

    @ApiOperation(value = "Eliminar proyecto", notes = "Elimina un proyecto existente por su ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Proyecto eliminado exitosamente"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 404, message = "Proyecto no encontrado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        deleteProjectUseCase.deleteProject(id, email);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Actualizar estado del proyecto", notes = "Actualiza el estado de un proyecto por su ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Estado del proyecto actualizado exitosamente"),
            @ApiResponse(code = 400, message = "Datos de entrada no válidos"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 404, message = "Proyecto no encontrado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @PatchMapping("/status/{id}")
    public ResponseEntity<ProjectOutputDto> updateProjectStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal String email,
            @Valid @RequestBody ProjectStatusUpdateInputDto inputDto) {
        ProjectOutputDto updated = updateProjectStatusUseCase.updateStatus(id, email, inputDto);
        return ResponseEntity.ok(updated);
    }

    @ApiOperation(value = "Listar proyectos", notes = "Obtiene una lista paginada de proyectos del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Listado de proyectos obtenido exitosamente"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @GetMapping("/list")
    public ResponseEntity<Page<ProjectOutputDto>> listProjects(
            @AuthenticationPrincipal String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProjectOutputDto> projects = listUserProjectsUseCase.listProjects(email, PageRequest.of(page, size));
        return ResponseEntity.ok(projects);
    }

    @ApiOperation(value = "Buscar proyectos por nombre", notes = "Busca proyectos del usuario autenticado filtrando por nombre, con paginación.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Proyectos encontrados"),
            @ApiResponse(code = 400, message = "Parámetros inválidos"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @GetMapping("/searchName")
    public ResponseEntity<Page<ProjectOutputDto>> searchProjects(
            @AuthenticationPrincipal String email,
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProjectOutputDto> projects = searchUserProjectsByNameUseCase.searchProjects(email, name, PageRequest.of(page, size));
        return ResponseEntity.ok(projects);
    }
}