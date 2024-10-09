package pl.kwojcik.project_lab.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kwojcik.project_lab.user.model.UserEntity;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long>, UserRepository {
    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
