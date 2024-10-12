package pl.kwojcik.project_lab.utils;

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
        var ctx = new SecurityContextImpl();

        ctx.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                "admin", "admin",
                List.of(new SimpleGrantedAuthority(AppPermission.ORDER_VIEW.getRole()))));
        SecurityContextHolder.setContext(ctx);

        var service = new PermissionCheckerService();
        service.checkPermissionsAndExecute(AppPermission.ORDER_VIEW, () -> {
            counter.incrementAndGet();
            return null;
        });

        //then
        assert counter.get() == 1;
    }

}