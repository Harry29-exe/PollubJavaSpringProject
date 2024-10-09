package pl.kwojcik.project_lab.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.kwojcik.project_lab.gen.api.dto.CreateUserCmdDTO;
import pl.kwojcik.project_lab.gen.api.dto.LoginCmdDTO;
import pl.kwojcik.project_lab.gen.api.dto.UserRoleDTO;
import pl.kwojcik.project_lab.user.UserRepository;
import pl.kwojcik.project_lab.user.UserService;
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

class AuthServiceTest {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);

    @Test
    void should_authenticate_existing_user() {
        //given
        var userService = new UserService(new MockedUserRepository(), passwordEncoder);
        var authService = new AuthService(userService, passwordEncoder);

        var user = new CreateUserCmdDTO()
                .role(UserRoleDTO.CUSTOMER)
                .username("bob")
                .password("123");
        userService.createUser(user);


        //when
        var token = authService.login(new LoginCmdDTO().username("bob").password("123"));

        //then
        var jwtAuth = new UsernamePasswordAuthenticationToken(token, null);
        var auth = authService.authenticate(jwtAuth);
        assert auth.isAuthenticated();
    }

}