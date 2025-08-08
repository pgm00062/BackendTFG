package freelancer.backendtfg.application.port.projectUseCasePort;

public interface DeleteProjectUseCase {
    void deleteProject(Long projectId, String userEmail);
} 