package com.shop.repository.search;

import com.shop.dto.BoardListAllDto;
import com.shop.dto.BoardListReplyCountDto;
import com.shop.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardSearch {

    Page<Board> search1(Pageable pageable);

    Page<Board> searchAll(String[] types, String keyword,Pageable pageable);

    Page<BoardListReplyCountDto> searchWithReplyCount(String[] types, String keyword,Pageable pageable);

    Page<BoardListAllDto> searchWithAll(String[] types,
                                        String keyword,
                                        Pageable pageable);

}
