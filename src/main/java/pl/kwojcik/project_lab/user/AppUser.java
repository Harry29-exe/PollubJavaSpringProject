package pl.kwojcik.project_lab.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.kwojcik.project_lab.user.model.AppPermission;
import pl.kwojcik.project_lab.user.model.AppRole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class AppUser implements UserDetails {
    private final String username;
    private final String passwordHash;
    private final AppRole appRole;

    public AppUser(String username, String passwordHash, AppRole role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.appRole = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //start L4 stream collection processing
        var authorities =  this.appRole.getPermissions().stream()
                .map(AppPermission::getRole)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(ArrayList::new));
        authorities.add(new SimpleGrantedAuthority(this.appRole.getRole()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
