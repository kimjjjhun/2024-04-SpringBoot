package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "member")
@Getter@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "roleSet")
public class Member  extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    private boolean del;

    private boolean social;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @CollectionTable(name = "member_roleSet", joinColumns = @JoinColumn(name = "member_id"))
    private Set<Role> roleSet = new HashSet<>();

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeDel(boolean del){
        this.del = del;
    }

    public void changeSocial(boolean social){
        this.social = social;
    }

    public void addRole(Role role){
        this.roleSet.add(role);
    }

    public void clearRoles(){
        this.roleSet.clear();
    }

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder, Set<Role> roles) {
        return Member.builder()
                .name(memberFormDto.getName())
                .email(memberFormDto.getEmail())
                .address(memberFormDto.getAddress())
                .password(passwordEncoder.encode(memberFormDto.getPassword()))
                .roleSet(roles)
                .build();
    }

}