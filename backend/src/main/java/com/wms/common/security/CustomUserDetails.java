package com.wms.common.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.wms.modules.system.entity.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private final SysUser user;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(SysUser user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public static CustomUserDetails of(SysUser user, String roleCode) {
        if (roleCode == null || roleCode.isEmpty()) {
            roleCode = "ROLE_OPERATOR";
        } else {
            roleCode = "ROLE_" + roleCode.toUpperCase();
        }
        return new CustomUserDetails(user, Collections.singletonList(new SimpleGrantedAuthority(roleCode)));
    }

    public Long getUserId() {
        return user.getId();
    }

    public SysUser getSysUser() {
        return user;
    }

    public String getRealName() {
        return user.getRealName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"locked".equals(user.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "active".equals(user.getStatus());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomUserDetails that = (CustomUserDetails) o;
        return Objects.equals(user.getId(), that.user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getId());
    }
}
