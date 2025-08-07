package freelancer.backendtfg.infrastructure.repository.port;

import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepositoryPort{
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);
    UserEntity save(UserEntity user);
    void delete(UserEntity user);
}
