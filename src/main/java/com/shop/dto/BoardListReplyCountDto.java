package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class BoardListReplyCountDto {

    private Long bno;

    private String title;

    private String writer;

    private LocalDateTime regTime;

    private Long replyCount;
}
