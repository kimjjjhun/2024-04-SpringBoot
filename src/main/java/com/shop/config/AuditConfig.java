package com.shop.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // JPA의 Auditing 기능을 활성화
public class AuditConfig {

    @Bean   // 클래스를 bean 으로 등록
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
