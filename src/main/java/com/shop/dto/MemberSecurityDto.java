package com.shop.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;


@Getter
@Setter
@ToString
public class MemberSecurityDto extends User implements OAuth2User {


    private String email;

    private String password;

    private String name;

    private String address;

    private boolean del;

    private boolean social;

    private Map<String,Object> props; // 소셜 로그인정보 확인


    public MemberSecurityDto(String username, String password, String name, boolean del, boolean social, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);

        this.email = username;
        this.password = password;
        this.name = name;
        this.del = del;
        this.social = this.social;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.getProps();
    }

    @Override
    public String getName() {
        return this.email;
    }
}
