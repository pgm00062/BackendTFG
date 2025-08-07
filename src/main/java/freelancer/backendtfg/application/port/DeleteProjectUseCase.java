package freelancer.backendtfg.application.port;

public interface DeleteProjectUseCase {
    void deleteProject(Long projectId, String userEmail);
} 