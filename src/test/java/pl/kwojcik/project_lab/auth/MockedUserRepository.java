package pl.kwojcik.project_lab.auth;

import pl.kwojcik.project_lab.user.UserRepository;
import pl.kwojcik.project_lab.user.model.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

class MockedUserRepository implements UserRepository {
    private List<UserEntity> users = new ArrayList<>();
    private AtomicLong nextId = new AtomicLong(1);

    @Override
    public UserEntity save(UserEntity userEntity) {
        userEntity.setId(nextId.getAndIncrement());
        users.add(userEntity);
        return userEntity;
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public boolean existsByUsername(String username) {
        return users.stream()
                .anyMatch(u -> u.getUsername().equals(username));
    }
}
