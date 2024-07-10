package com.shop.service;


import com.shop.constant.Role;
import com.shop.dto.MemberSecurityDto;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class Oauth2MemberService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();

        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String,Object> paramMap = oAuth2User.getAttributes();

        String email = null;
        String nickname = null;

        switch (clientName){
            case "kakao":
                email = getKakaoEmail(paramMap);
                nickname = getKakaoNickname(paramMap);
                break;
        }

        return generateDto(email,nickname,paramMap);
    }

    private MemberSecurityDto generateDto(String email, String nickname, Map<String, Object> params) {

        Optional<Member> result = memberRepository.findByEmail(email);

        if(result.isEmpty()){
            Member member = Member.builder()
                    .email(email)
                    .password(passwordEncoder.encode("1234"))
                    .email(email)
                    .name(nickname)
                    .social(true)
                    .build();

            member.addRole(Role.USER);
            memberRepository.save(member);

            //MemberSecurityDto 구성및반환
            MemberSecurityDto memberSecurityDto = new MemberSecurityDto(
                    email,
                    "1234",
                    nickname,
                    true,
                    member.isSocial(),
                    Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));

            memberSecurityDto.setProps(params);

            return memberSecurityDto;
        }else {
            Member member = result.get();
            MemberSecurityDto memberSecurityDto = new MemberSecurityDto(
                    member.getEmail(),
                    member.getPassword(),
                    member.getName(),
                    member.isDel(),
                    member.isSocial(),
                    member.getRoleSet().stream().map(Role -> new SimpleGrantedAuthority("ROLE_"+Role.name())).collect(Collectors.toList())
            );

            log.info("=====================================");
            log.info("memberSecurityDto: " , memberSecurityDto);
            log.info("=====================================");
            return memberSecurityDto;
        }

    }

    private String getKakaoEmail(Map<String,Object> paramMap){

        log.info("=================KAKAO======================================");

        Object value = paramMap.get("kakao_account");

        log.info(value);
        log.info("============================================================");

        LinkedHashMap accountMap = (LinkedHashMap)value;

        String email = (String)accountMap.get("email");

        log.info("============================================================");
        log.info(email);
        log.info("============================================================");

        return email;
    }
    private String getKakaoNickname(Map<String, Object> paramMap) {

        log.info("=================KAKAO NICKNAME==============================");

        Object value = paramMap.get("properties");

        log.info("============================================================");
        log.info(value);
        log.info("============================================================");

        LinkedHashMap propertiesMap = (LinkedHashMap) value;

        String nickname = (String) propertiesMap.get("nickname");

        log.info("============================================================");
        log.info(nickname);
        log.info("============================================================");

        return nickname;
    }
}
