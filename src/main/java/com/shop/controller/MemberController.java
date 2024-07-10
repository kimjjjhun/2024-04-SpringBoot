package com.shop.controller;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @GetMapping("/new")
    public String newMember(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping("/new")
    public String memberForm(@Valid MemberFormDto memberFormDto,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "member/memberForm";
        }

        try {
            Set<Role> roles = new HashSet<>();
            roles.add(Role.USER); // 기본 역할 설정
            if (memberFormDto.getEmail().equals("admin@example.com")) {
                roles.add(Role.ADMIN); // 관리자 역할 추가
            }
            Member member = Member.createMember(memberFormDto, passwordEncoder, roles);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginMember() {
        return "member/memberLoginForm";
    }

    @GetMapping("/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        return "member/memberLoginForm";
    }

    @GetMapping("/mypage")
    public String mypage(Principal principal, Model model) {
        String username = principal.getName(); // 현재 로그인한 사용자의 이메일 가져오기
        Optional<Member> memberOptional = memberRepository.findByEmail(username); // 이메일을 기반으로 회원 정보 조회

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            model.addAttribute("member", member); // 모델에 회원 정보 추가
            return "member/mypage"; // 마이페이지 화면으로 이동
        } else {
            return "redirect:/"; // 회원 정보가 없으면 홈페이지로 리다이렉트
        }
    }

    @PostMapping("/mypage")
    public String updateMypage(@ModelAttribute MemberFormDto memberUpdateDto, Principal principal) {
        String username = principal.getName();
        Optional<Member> memberOptional = memberRepository.findByEmail(username);

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            // 업데이트할 필드 설정
            member.setName(memberUpdateDto.getName());
            member.setEmail(memberUpdateDto.getEmail());
            member.setAddress(memberUpdateDto.getAddress());

            memberRepository.save(member); // 변경된 회원 정보 저장

            return "redirect:/mypage"; // 수정 완료 후 마이페이지로 리다이렉트
        } else {
            return "redirect:/"; // 회원 정보가 없는 경우 처리 방법에 따라 리다이렉트 처리
        }
    }
}