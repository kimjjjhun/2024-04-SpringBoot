package com.shop.entity;

import com.shop.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Log4j2
class MemberTest {

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("Auditing 테스트")
    @WithMockUser(username = "gildong" , roles = "USER")    // 시큐리티 어노테이션 , 가상의 계정을 로그인한 상태로 만들어서 테스트 할 수 있다.
    public void auditingTest(){
    Member newMember = new Member();
    memberRepository.save(newMember);

    em.flush();
    em.clear();

        Member member = memberRepository.findById(newMember.getId()).orElseThrow(EntityNotFoundException::new);
        log.info("=======================Auditing 테스트====================================");
        log.info("register time : " + member.getRegTime());
        log.info("getUpdate Time : " + member.getUpdateTime());
        log.info("getCreatedBy : " + member.getCreatedBy());
        log.info("getModifiedBy : " + member.getModifiedBy());
    }
}