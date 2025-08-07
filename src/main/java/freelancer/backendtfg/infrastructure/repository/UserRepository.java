package freelancer.backendtfg.infrastructure.repository;

import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.JpaUserRepository;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository implements UserRepositoryPort {
    private final JpaUserRepository jpaRepository;

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return jpaRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByDni(String dni) {
        return jpaRepository.existsByDni(dni);
    }

    @Override
    public UserEntity save(UserEntity user) {
        return jpaRepository.save(user);
    }

    @Override
    public void delete(UserEntity user) {
        jpaRepository.delete(user);
    }
}
