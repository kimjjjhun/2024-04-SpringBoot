package com.shop.service;

import com.shop.dto.ReplyDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Log4j2
class ReplyServiceTest {

    @Autowired
    private ReplyService replyService;

    @Test
    @DisplayName("댓글 등록 테스트")
    public void testRegister(){

        ReplyDto replyDto = ReplyDto.builder()
                .replyText("ReplyDto Text")
                .replyer("Replyer")
                .bno(1L)
                .build();

        log.info(replyService.register(replyDto));
    }

}