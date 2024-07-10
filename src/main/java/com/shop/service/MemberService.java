package com.shop.service;

import com.shop.entity.CartItem;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public Member saveMember(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByEmail(member.getEmail())
                .ifPresent(existingMember -> {
                    throw new IllegalStateException("이미 가입된 회원입니다.");
                });
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("--------------------loadUserByUsername---------------------------------");
        log.info(email);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // 사용자 역할(Role)을 문자열로 변환하여 배열로 만듭니다
        String[] rolesArray = member.getRoleSet().stream()
                .map(Enum::name)
                .toArray(String[]::new);

        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(rolesArray) // 역할(Role)을 배열 형태로 설정합니다
                .build();
    }
}