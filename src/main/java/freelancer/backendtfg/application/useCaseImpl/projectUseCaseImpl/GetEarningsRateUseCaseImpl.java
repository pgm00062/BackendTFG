package freelancer.backendtfg.application.useCaseImpl.projectUseCaseImpl;

import freelancer.backendtfg.application.port.projectUseCasePort.GetEarningsRateUseCase;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.EarningsRateSummaryOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectEarningsRateOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetEarningsRateUseCaseImpl implements GetEarningsRateUseCase {

    private final ProjectRepositoryPort projectRepositoryPort;
    private final TimeRepositoryPort timeRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public EarningsRateSummaryOutputDto getEarningsRate(String email) {
        // Validar que el usuario existe
        UserEntity user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // Obtener todos los proyectos del usuario (sin paginación para el cálculo)
        Page<ProjectEntity> projectsPage = projectRepositoryPort.findByUserId(
                user.getId(), 
                PageRequest.of(0, Integer.MAX_VALUE));
        List<ProjectEntity> projects = projectsPage.getContent();

        // Calcular la tasa de ganancia para cada proyecto
        List<ProjectEarningsRateOutputDto> projectRates = new ArrayList<>();
        BigDecimal totalBudget = BigDecimal.ZERO;
        double totalHours = 0.0;

        for (ProjectEntity project : projects) {
            // Obtener todas las sesiones de tiempo completadas del proyecto
            List<TimeEntity> timeSessions = timeRepositoryPort
                    .findCompletedSessionsByProjectIdAndUserEmail(project.getId(), email);

            // Calcular total de horas trabajadas en el proyecto
            double projectHours = timeSessions.stream()
                    .mapToDouble(TimeEntity::getDurationInHours)
                    .sum();

            // Calcular tasa de ganancia por hora (€/hora)
            BigDecimal earningsPerHour = BigDecimal.ZERO;
            if (projectHours > 0) {
                earningsPerHour = project.getBudget()
                        .divide(BigDecimal.valueOf(projectHours), 2, RoundingMode.HALF_UP);
            }

            // Crear DTO del proyecto
            ProjectEarningsRateOutputDto projectDto = ProjectEarningsRateOutputDto.builder()
                    .projectId(project.getId())
                    .projectName(project.getName())
                    .budget(project.getBudget())
                    .totalHours(projectHours)
                    .earningsPerHour(earningsPerHour)
                    .status(project.getStatus().name())
                    .build();

            projectRates.add(projectDto);

            // Acumular totales
            totalBudget = totalBudget.add(project.getBudget());
            totalHours += projectHours;
        }

        // Calcular la media de ganancias por hora
        BigDecimal averageEarningsPerHour = BigDecimal.ZERO;
        if (totalHours > 0) {
            averageEarningsPerHour = totalBudget
                    .divide(BigDecimal.valueOf(totalHours), 2, RoundingMode.HALF_UP);
        }

        // Construir el resumen
        return EarningsRateSummaryOutputDto.builder()
                .averageEarningsPerHour(averageEarningsPerHour)
                .totalHours(totalHours)
                .totalBudget(totalBudget)
                .totalProjects(projects.size())
                .projectRates(projectRates)
                .build();
    }
}
