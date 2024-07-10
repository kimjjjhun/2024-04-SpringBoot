package com.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class BoardListAllDto {

    private Long bno;

    private String title;

    private String writer;

    private LocalDateTime regTime;

    private Long replyCount;

    private List<BoardImageDto> boardImages;
}
