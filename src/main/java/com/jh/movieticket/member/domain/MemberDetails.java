package com.jh.movieticket.member.domain;

import java.util.ArrayList;
import java.util.Collection;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor
public class MemberDetails implements UserDetails {

    private final Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> member.getRole().name());

        return authorities;
    }

    @Override
    public String getPassword() {

        return member.getUserPW();
    }

    @Override
    public String getUsername() {

        return member.getUserId();
    }
}
