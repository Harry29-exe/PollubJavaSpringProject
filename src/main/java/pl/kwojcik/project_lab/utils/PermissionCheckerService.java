package pl.kwojcik.project_lab.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.kwojcik.project_lab.user.AppUser;
import pl.kwojcik.project_lab.user.model.AppPermission;

@Service
public class PermissionCheckerService {
    //start L4 functional interface
    public interface SecuredFunc<T> {
        T execute();
    }

    public <T> T checkPermissionsAndExecute(AppPermission neededPermission, SecuredFunc<T> func) {
        var user = SecurityContextHolder.getContext().getAuthentication();
        //start L4 stream collection processing
        var hasPermission = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals(neededPermission.getRole()));
        if (!hasPermission) {
            throw new IllegalStateException("You do not have permission to perform this action");
        }
        return func.execute();
    }
}
