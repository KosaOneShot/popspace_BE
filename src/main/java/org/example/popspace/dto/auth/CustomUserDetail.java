package org.example.popspace.dto.auth;


import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class CustomUserDetail implements UserDetails {

    private final Long id;
    private final String email;
    private final String nickname;
    @ToString.Exclude
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public static CustomUserDetail from(MemberLoginInfo member){
        List<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(member.getRole()));
        return CustomUserDetail.builder()
                .id(member.getMemberId())
                .email(member.getEmail())
                .password(member.getPassword())
                .authorities(roles)
                .nickname(member.getNickname())
                .build();
    }

    public String getRole() {
        return authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);
    }

    public Long getUserDetailId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
