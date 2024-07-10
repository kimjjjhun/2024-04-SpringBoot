package com.shop.config.handler;


import com.shop.dto.MemberSecurityDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class CumstomSocailLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("=======================================================");
        log.info("CustomLoginSuccessHandler onAuthenticationSuccess");
        log.info(authentication.getPrincipal());
        log.info("=======================================================");

        MemberSecurityDto memberSecurityDto = (MemberSecurityDto) authentication.getPrincipal();

        String encodePw =  memberSecurityDto.getPassword();


        //소셜로그인이고 패스워드가 1234
        if(memberSecurityDto.isSocial() && (memberSecurityDto.getPassword().equals("1234")
                ||passwordEncoder.matches("1234", memberSecurityDto.getPassword())
        )) {
            log.info("Should Change Password");

            log.info("Redirect to Member Modify");
            response.sendRedirect("/member/modify");

            return;
        }else {
            response.sendRedirect("/board/list");
        }

    }
}
