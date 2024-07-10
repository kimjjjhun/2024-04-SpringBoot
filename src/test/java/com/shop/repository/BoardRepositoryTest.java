package com.shop.repository;

import com.shop.dto.BoardListAllDto;
import com.shop.dto.BoardListReplyCountDto;
import com.shop.dto.PageRequestDto;
import com.shop.dto.PageResponseDto;
import com.shop.entity.Board;
import com.shop.entity.BoardImage;
import com.shop.service.BoardService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private BoardService boardService;

    @Test
    @DisplayName("게시글 등록 테스트")
    public void testInsert(){
        IntStream.rangeClosed(1,100).forEach(i->{
            Board board = Board.builder()
                    .title("title......" + i)
                    .content("content......" + i)
                    .writer("user" + (i % 10))
                    .build();

            Board result = boardRepository.save(board);
            log.info("BNO: " + result.getBno());
        });
    }

    @Test
    @DisplayName("게시글 조회 테스트")
    public void testSelect(){
        Long bno = 200L;

        Optional<Board> result = boardRepository.findById(bno);

        Board board = result.orElseThrow();

        log.info("BOARD: " + board);
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    public void testUpdate(){
        Long bno = 200L;
        Optional<Board> result = boardRepository.findById(bno);
        Board board = result.orElseThrow();
        board.change("update.....title100","update.....content100");
        boardRepository.save(board);
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    public void testDelete(){
        Long bno = 200L;
        boardRepository.deleteById(bno);
    }

    @Test
    @DisplayName("페이징 테스트")
    public void testPaging(){
        //1 page order by bno desc
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.findAll(pageable);
        log.info("total count: " + result.getTotalElements());
        log.info("total pages: " + result.getTotalPages());
        log.info("page number: " + result.getNumber());
        log.info("page size: " + result.getSize());

        List<Board> todoList = result.getContent();
        todoList.forEach(board -> log.info(board));
    }

    @Test
    @DisplayName("board search 테스트")
            public void testSearch1(){
    //2 page order by bno desc
    Pageable pageable = PageRequest.of(1,10,Sort.by("bno").descending());
    boardRepository.search1(pageable);
    }

    @Test
    public void testSearchAll(){
        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types,keyword,pageable);
    }
    @Test
    public void testSearchAll2(){
        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types,keyword,pageable);

        //total pages
        log.info(result.getTotalPages());

        //page size
        log.info(result.getSize());

        //page Number
        log.info(result.getNumber());

        // prev next
        log.info(result.hasPrevious() + " : " + result.hasNext());

        result.getContent().forEach(board-> log.info(board));
    }

    @Test
    public void testSearchReplyCount(){

        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<BoardListReplyCountDto> result = boardRepository.searchWithReplyCount(types,keyword,pageable);
        //total Page
        log.info(result.getTotalPages());
        //page size
        log.info(result.getSize());
        //pageNumber
        log.info(result.getNumber());
        //prev next
        log.info(result.hasPrevious() + ":" + result.hasNext());

        result.getContent().forEach(board -> log.info(board));
    }

    @Test
    public void testInsertWithImages(){

        Board board = Board.builder()
                .title("title")
                .content("첨부파일테스트...")
                .writer("user1")
                .build();

        for(int i=0; i < 3; i++){

            board.addImage(UUID.randomUUID().toString(), "file" + i + ".jpg");
        }// end for

        boardRepository.save(board);
    }

    @Test
    public void testReadWithImages(){
        //반드시 존재하는 bno로 확인
        Optional<Board> result = boardRepository.findByIdWithImages(1L);

        Board board = result.orElseThrow();

        log.info(board);
        log.info("----------------");
        for(BoardImage boardImage : board.getImageSet()){
            log.info(boardImage);
        }
    }

    @Transactional
    @Commit
    @Test
    public void testModifyImages(){

        Optional<Board> result = boardRepository.findByIdWithImages(1L);

        Board board = result.orElseThrow();

        //기존의 첨부파일들은 삭제
        board.clearImages();

        //새로운 첨부파일 등록
        for(int i = 0; i < 2; i++){
            board.addImage(UUID.randomUUID().toString(), "file" + i + ".jpg");
        }

        boardRepository.save(board);
    }

    @Test
    @Transactional
    @Commit
    public void testRemoveAll(){
        Long bno = 1L;

        replyRepository.deleteByBoard_bno(bno);

        boardRepository.deleteById(bno);
    }

    @Test
    public void testInsertAll(){
        for(int i=0; i < 100; i++){
            Board board = Board.builder()
                    .title("Title.."+i)
                    .content("Content"+i)
                    .writer("writer.."+i)
                    .build();

            for(int j = 0; j < 3; j++){

                if(i % 5 == 0){
                    continue;
                }
                board.addImage(UUID.randomUUID().toString(),i+"file"+j+".jpg");
            }
            boardRepository.save(board);
        }//end for
    }

    @Transactional
    @Test
    public void testSearchImageReplyCount(){

        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());

//        boardRepository.searchAll(null,null,pageable);

        Page<BoardListAllDto> result = boardRepository.searchWithAll(null,null,pageable);

        log.info("----------------------------");
        log.info(result.getTotalElements());

        result.getContent().forEach(boardListAllDto -> log.info(boardListAllDto));
    }

}