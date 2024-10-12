package pl.kwojcik.project_lab.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.kwojcik.project_lab.gen.api.dto.CreateUserCmdDTO;
import pl.kwojcik.project_lab.gen.api.dto.UserRoleDTO;
import pl.kwojcik.project_lab.user.UserService;


public class UserServiceTest {
    @Test
    void should_return_user_that_exit() {
        //given
        var userService = new UserService(new MockedUserRepository(), new BCryptPasswordEncoder(6));
        var user = createBobUser();
        userService.createUser(user);

        //when
        var foundUser = userService.loadUserByUsername("bob");

        //then
        assert foundUser.getUsername().equals(user.getUsername());
    }

    @Test
    void should_return_user_that_exit_when_multiple_users_exist() {
        //given
        var userService = new UserService(new MockedUserRepository(), new BCryptPasswordEncoder(6));
        var bob = createBobUser();
        userService.createUser(bob);
        var alice = createAliceUser();
        userService.createUser(alice);

        //when
        var foundUser = userService.loadUserByUsername("bob");

        //then
        assert foundUser.getUsername().equals(bob.getUsername());
    }

    @Test
    void should_not_return_non_exiting_user() {
        //given
        var userService = new UserService(new MockedUserRepository(), new BCryptPasswordEncoder(6));
        var bob = createBobUser();
        userService.createUser(bob);
        var alice = createAliceUser();
        userService.createUser(alice);

        //when
        Assertions.assertThrows(Exception.class, () -> {
            //then
            userService.loadUserByUsername("george");
        });
    }

    @Test
    void should_create_user() {
        //given
        var userService = new UserService(new MockedUserRepository(), new BCryptPasswordEncoder(6));

        //when
        var bob = createBobUser();
        userService.createUser(bob);

        //then
        assert userService.loadUserByUsername("bob") != null;
    }


    @Test
    void should_create_user_when_other_users_exist() {
        //given
        var userService = new UserService(new MockedUserRepository(), new BCryptPasswordEncoder(6));
        var alice = createAliceUser();
        userService.createUser(alice);

        //when
        var bob = createBobUser();
        userService.createUser(bob);

        //then
        assert userService.loadUserByUsername("bob") != null;
    }

    private CreateUserCmdDTO createBobUser() {
        return new CreateUserCmdDTO()
                .role(UserRoleDTO.CUSTOMER)
                .username("bob")
                .password("123");
    }

    private CreateUserCmdDTO createAliceUser() {
        return new CreateUserCmdDTO()
                .role(UserRoleDTO.CUSTOMER)
                .username("alice")
                .password("321");
    }
}
