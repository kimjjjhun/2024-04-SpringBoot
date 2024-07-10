package com.shop.service;

import com.shop.dto.*;
import com.shop.entity.Board;
import com.shop.repository.BoardRepository;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class BoardService {

    private final BoardRepository boardRepository;

    private final ModelMapper modelMapper;

    Board dtoToEntity(BoardDto boardDto){

        Board board = Board.builder()
                .bno(boardDto.getBno())
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .writer(boardDto.getWriter())
                .build();

        if(boardDto.getFileNames() != null){
            boardDto.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_");
                board.addImage(arr[0],arr[1]);
            });
        }
        return board;
    }

    BoardDto entityToDto(Board board){

        BoardDto boardDto = BoardDto.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .regTime(board.getRegTime())
                .updateTime(board.getUpdateTime())
                .build();

        List<String> fileNames =
                board.getImageSet().stream().sorted().map(boardImage -> boardImage.getUuid()+"_"+boardImage.getFileName()).collect(Collectors.toList());

        boardDto.setFileNames(fileNames);

        return boardDto;
    }


    public Long register(BoardDto boardDto) {

        Board board = dtoToEntity(boardDto);

        Long bno = boardRepository.save(board).getBno();

        return bno;
    }

    public BoardDto readOne(Long bno){
        //board_image까지 조인 처리되는 findByWithImages()를 이용
        Optional<Board> result = boardRepository.findByIdWithImages(bno);

        Board board = result.orElseThrow();

        BoardDto boardDto = entityToDto(board);

        return boardDto;

    }

    public void modify(BoardDto boardDto) {

        Optional<Board> result = boardRepository.findById(boardDto.getBno());

        Board board = result.orElseThrow();

        board.change(boardDto.getTitle(),boardDto.getContent());

        //첨부파일 처리
        board.clearImages();

        if(boardDto.getFileNames() != null){
            for (String fileName : boardDto.getFileNames()){
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);

            }
        }

        boardRepository.save(board);
    }

    public void remove(Long bno)
    {
        boardRepository.deleteById(bno);
    }

    public PageResponseDto<BoardDto> list(PageRequestDto pageRequestDto) {

        String[] types = pageRequestDto.getTypes();
        String keyword = pageRequestDto.getKeyword();
        Pageable pageable = pageRequestDto.getPageable("bno");

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        List<BoardDto> dtoList = result.getContent().stream()
                .map(board->modelMapper.map(board,BoardDto.class)).collect(Collectors.toList());

        return PageResponseDto.<BoardDto>withAll()
                .pageRequestDto(pageRequestDto)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }

    public PageResponseDto<BoardListReplyCountDto> ListWithReplyCount(PageRequestDto pageRequestDto) {
        String[] types = pageRequestDto.getTypes();
        String keyword = pageRequestDto.getKeyword();
        Pageable pageable = pageRequestDto.getPageable("bno");

        Page<BoardListReplyCountDto> result = boardRepository.searchWithReplyCount(types, keyword, pageable);

        return PageResponseDto.<BoardListReplyCountDto>withAll()
                .pageRequestDto(pageRequestDto)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    public PageResponseDto<BoardListAllDto> listWithAll(PageRequestDto pageRequestDto){

        String[] types = pageRequestDto.getTypes();
        String keyword = pageRequestDto.getKeyword();
        Pageable pageable = pageRequestDto.getPageable("bno");

        Page<BoardListAllDto> result = boardRepository.searchWithAll(types,keyword,pageable);

        return PageResponseDto.<BoardListAllDto>withAll()
                .pageRequestDto(pageRequestDto)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }


}
