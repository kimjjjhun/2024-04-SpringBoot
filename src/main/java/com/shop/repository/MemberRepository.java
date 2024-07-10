package com.shop.repository;

import com.shop.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberRepository  extends JpaRepository<Member, Long> {

//    Member findByEmail(String email);

    @EntityGraph(attributePaths = "roleSet")
    @Query("select m from Member m where m.email = :email and m.social = false")
    Optional<Member> getWithRoles(String email);

    @EntityGraph(attributePaths = "roleSet")
    Optional<Member> findByEmail(String email);

    @Modifying      // modifying 을 사용하면 select + update , insert , delete 도 가능하다.
    @Transactional
    @Query("update Member m set m.password = :password where m.email = :email")
    void updatePassword(@Param("email")String email,@Param("password")String password);
}