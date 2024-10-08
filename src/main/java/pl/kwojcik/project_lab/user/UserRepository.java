package pl.kwojcik.project_lab.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kwojcik.project_lab.user.model.UserEntity;

import java.util.Optional;

public interface UserRepository  {
    UserEntity save(UserEntity userEntity);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
