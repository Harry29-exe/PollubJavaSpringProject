package pl.kwojcik.project_lab.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.kwojcik.project_lab.gen.api.dto.CreateUserCmdDTO;
import pl.kwojcik.project_lab.gen.api.dto.LoginCmdDTO;
import pl.kwojcik.project_lab.gen.api.dto.UserRoleDTO;
import pl.kwojcik.project_lab.user.UserService;
import pl.kwojcik.project_lab.user.model.AppPermission;
import pl.kwojcik.project_lab.user.model.AppRole;

import java.util.*;
import java.util.stream.Collectors;

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

    @Test
    void should_authenticate_and_return_specific_permission() {
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
        for (var expectedPermission : AppRole.CUSTOMER.getPermissions()) {
            assert auth.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(a -> a.equals(expectedPermission.getRole()));
        }
    }

    @Test
    void should_authenticate_and_not_return_permission_that_user_does_not_have() {
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

        var allowedPermissions = new HashSet<>(AppRole.CUSTOMER.getPermissions());
        var notAllowedPermissions = Arrays.stream(AppPermission.values())
                .filter(p -> !allowedPermissions.contains(p))
                .map(AppPermission::name)
                .collect(Collectors.toSet());

        for (var userPermission : auth.getAuthorities()) {
            System.out.println(userPermission);
            assert !notAllowedPermissions.contains(userPermission.getAuthority());
        }
    }

    @Test
    void should_not_authenticate_not_existing_user() {
        //given
        var userService = new UserService(new MockedUserRepository(), passwordEncoder);
        var authService = new AuthService(userService, passwordEncoder);
        var user = new CreateUserCmdDTO()
                .role(UserRoleDTO.CUSTOMER)
                .username("bob")
                .password("123");
        userService.createUser(user);

        //then
        Assertions.assertThrows(Exception.class, () -> {
            //when
            authService.login(new LoginCmdDTO().username("not_exist_user").password("123"));
        });
    }

    @Test
    void should_not_authenticate_not_user_with_wrong_password() {
        //given
        var userService = new UserService(new MockedUserRepository(), passwordEncoder);
        var authService = new AuthService(userService, passwordEncoder);
        var user = new CreateUserCmdDTO()
                .role(UserRoleDTO.CUSTOMER)
                .username("bob")
                .password("123");
        userService.createUser(user);

        //then
        Assertions.assertThrows(Exception.class, () -> {
            //when
            authService.login(new LoginCmdDTO().username("bon").password("321"));
        });
    }
}