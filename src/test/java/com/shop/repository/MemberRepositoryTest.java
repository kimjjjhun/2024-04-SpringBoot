package com.shop.repository;

import com.shop.constant.Role;
import com.shop.entity.Member;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Log4j2
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Test
    public void insertMember(){
        IntStream.rangeClosed(1,100).forEach(i -> {
            Member member = Member.builder()
                    .email("member"+i)
                    .password(passwordEncoder.encode("1234"))
                    .name("test"+i)
                    .address("우리집"+i)
                    .build();

            member.addRole(Role.USER);

            if(i >= 90){
                member.addRole(Role.ADMIN);
            }
            memberRepository.save(member);

        });
    }

    @Test
    public void testRead(){

        Optional<Member> result = memberRepository.getWithRoles("member100");

        Member member = result.orElseThrow();

        log.info(member);
        log.info(member.getRoleSet());
        member.getRoleSet().forEach(Role -> log.info(Role.name()));
    }

    @Test
    public void testUpdate(){

        String email = "kjhun2727@gmail.com";
        String password = passwordEncoder.encode("54325");

        memberRepository.updatePassword(password , email);

    }

}