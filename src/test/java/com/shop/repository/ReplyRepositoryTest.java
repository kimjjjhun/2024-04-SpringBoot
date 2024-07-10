package com.shop.repository;

import com.shop.dto.BoardListReplyCountDto;
import com.shop.entity.Board;
import com.shop.entity.Reply;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Log4j2
class ReplyRepositoryTest {

    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName("댓글 등록 테스트")
    public void testInsertMultipleReplies() {
        Long bno = 1L;

        Board board = Board.builder().bno(bno).build();

        for (int i = 0; i < 100; i++) {
            Reply reply = Reply.builder()
                    .board(board)
                    .replyText("댓글 " + i)
                    .replyer("작성자 " + i)
                    .build();

            replyRepository.save(reply);
        }
    }

    @Test
    @DisplayName("댓글 조회 테스트")
    public void testBoardreplies(){
        Long bno = 1L;

        Pageable pageable = PageRequest.of(0,10, Sort.by("rno").descending());

        Page<Reply> result = replyRepository.listOfBoard(bno,pageable);

        result.getContent().forEach(reply -> {log.info(reply);
        });
    }

}