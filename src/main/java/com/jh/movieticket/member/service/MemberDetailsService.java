package com.jh.movieticket.member.service;

import com.jh.movieticket.member.domain.Member;
import com.jh.movieticket.member.domain.MemberDetails;
import com.jh.movieticket.member.exception.MemberErrorCode;
import com.jh.movieticket.member.exception.MemberException;
import com.jh.movieticket.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByUserIdAndDeleteDate(username, null)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        return new MemberDetails(member);
    }
}
