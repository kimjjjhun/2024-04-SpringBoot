package com.shop.service;

import com.shop.dto.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Log4j2
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Test
    public void testRegister(){
        log.info(boardService.getClass().getName());

        BoardDto boardDto = BoardDto.builder()
                .bno(200L)
                .title("Sample Title....")
                .content("Sample Content...")
                .writer("Sample Writer")
                .build();

        Long bno = boardService.register(boardDto);

        log.info("bno :" + bno);
    }

    @Test
    @DisplayName("수정 테스트")
    public void testModify(){
        BoardDto boardDto = BoardDto.builder()
                .bno(203L)
                .title("update......title")
                .content("update......content")
                .build();

        boardService.modify(boardDto);
    }

    @Test
    public void testList(){

        PageRequestDto pageRequestDto = PageRequestDto.builder()
                .type("tcw")
                .keyword("1")
                .page(1)
                .size(10)
                .build();

        PageResponseDto<BoardDto> responseDto = boardService.list(pageRequestDto);

        log.info(responseDto);
    }

    @Test
    public void testRegisterwithImages(){

        log.info(boardService.getClass().getName());

        BoardDto boardDto = BoardDto.builder()
                .title("File...Sample Title....")
                .content("Sample Content...")
                .writer("Sample Writer")
                .build();

        boardDto.setFileNames(
                Arrays.asList(
                        UUID.randomUUID()+"_aaa.jpg",
                        UUID.randomUUID()+"_bbb.jpg",
                        UUID.randomUUID()+"_ccc.jpg"
                ));

        Long bno = boardService.register(boardDto);

        log.info("bno:" + bno);
    }

    @Test
    public void testReadAll(){

        Long bno = 4L;

        BoardDto boardDto = boardService.readOne(bno);

        log.info(boardDto);

        for(String fileName : boardDto.getFileNames()){
            log.info(fileName);
        }// end for
    }

    @Test
    public void testModify2(){

        //변경에 필요한 데이터
        BoardDto boardDto = BoardDto.builder()
                .bno(1L)
                .title("update......title")
                .content("update......content")
                .build();

        //첨부파일 하나 추가
        boardDto.setFileNames(Arrays.asList(UUID.randomUUID()+"_zzz.jpg"));

        boardService.modify(boardDto);

    }

    @Test
    public void testDelete(){
        Long bno = 1L;

        boardService.remove(bno);
    }

    @Transactional
    @Test
    public void testListWithAll(){
        PageRequestDto pageRequestDto = PageRequestDto.builder()
                .page(1)
                .size(10)
                .build();

        PageResponseDto<BoardListAllDto> responseDto =

                boardService.listWithAll(pageRequestDto);

        List<BoardListAllDto> dtoList = responseDto.getDtoList();

        dtoList.forEach(boardListAllDto -> {
            log.info(boardListAllDto.getBno()+":"+boardListAllDto.getTitle());

            if(boardListAllDto.getBoardImages() != null){
                for(BoardImageDto boardImage : boardListAllDto.getBoardImages()){
                    log.info(boardImage);
                }
            }

            log.info("_________________________________________");
        });
    }

}