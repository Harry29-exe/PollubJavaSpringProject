package pl.kwojcik.project_lab.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import pl.kwojcik.project_lab.user.model.AppPermission;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class PermissionCheckerServiceTest {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Test
    void should_allow_for_method_execution() {
        setPermission(List.of(AppPermission.ORDER_VIEW));

        var service = new PermissionCheckerService();
        service.checkPermissionsAndExecute(AppPermission.ORDER_VIEW, () -> {
            counter.incrementAndGet();
            return null;
        });

        //then
        assert counter.get() == 1;
    }

    @Test
    void should_allow_for_method_execution_while_having_multiple_permissions() {
        setPermission(List.of(AppPermission.ORDER_VIEW, AppPermission.ORDER_MODIFY));

        var service = new PermissionCheckerService();
        service.checkPermissionsAndExecute(AppPermission.ORDER_VIEW, () -> {
            counter.incrementAndGet();
            return null;
        });

        //then
        assert counter.get() == 1;
    }

    @Test
    void should_not_allow_while_having_none_roles() {
        setPermission(List.of());

        var service = new PermissionCheckerService();

        //then
        Assertions.assertThrows(Exception.class, () -> {
            //when
            service.checkPermissionsAndExecute(AppPermission.ORDER_VIEW, () -> {
                counter.incrementAndGet();
                return null;
            });
        });
    }

    @Test
    void should_not_allow_while_having_one_different_role() {
        setPermission(List.of(AppPermission.ORDER_MODIFY));

        var service = new PermissionCheckerService();

        //then
        Assertions.assertThrows(Exception.class, () -> {
            //when
            service.checkPermissionsAndExecute(AppPermission.ORDER_VIEW, () -> {
                counter.incrementAndGet();
                return null;
            });
        });
    }

    @Test
    void should_not_allow_while_having_multiple_different_role() {
        setPermission(List.of(AppPermission.ORDER_MODIFY, AppPermission.ADMIN_USER_CREATE));

        var service = new PermissionCheckerService();

        //then
        Assertions.assertThrows(Exception.class, () -> {
            //when
            service.checkPermissionsAndExecute(AppPermission.ORDER_VIEW, () -> {
                counter.incrementAndGet();
                return null;
            });
        });
    }

    private static void setPermission(List<AppPermission> permissions) {
        var ctx = new SecurityContextImpl();

        ctx.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                "admin",
                "admin",
                permissions.stream()
                        .map(p ->
                                new SimpleGrantedAuthority(p.getRole()))
                        .toList())
        );
        SecurityContextHolder.setContext(ctx);
    }

}