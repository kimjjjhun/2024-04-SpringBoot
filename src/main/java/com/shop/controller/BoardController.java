package com.shop.controller;


import com.shop.constant.Role;
import com.shop.dto.*;
import com.shop.entity.Board;
import com.shop.service.BoardService;
import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Controller
@Log4j2
@RequestMapping(value = "/board")
@RequiredArgsConstructor
public class BoardController {

    @Value("${com.shop.upload.path}") // import 시에 springframework으로 시작하는 value
    private String uploadPath;

    private final BoardService boardService;

    @GetMapping(value = "/notice")
    public void notice(){

        log.info("board Get notice...........");

    }

    @GetMapping(value = "/list")
    public void list(PageRequestDto pageRequestDto, Model model) {

        PageResponseDto<BoardListAllDto> responseDto = boardService.listWithAll(pageRequestDto);

            model.addAttribute("responseDto", responseDto);

    }

    @GetMapping(value = "/register")
    public void register(){

        log.info("board Get register...........");

    }

    @PostMapping(value = "/register")
    public String register(@Valid BoardDto boardDto, BindingResult bindingResult, RedirectAttributes redirectAttributes){

        log.info("board Post register...........");

        if(bindingResult.hasErrors()){
            log.info("has error.......");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/register";
        }

        log.info(boardDto);

        Long bno = boardService.register(boardDto);

        redirectAttributes.addFlashAttribute("result", bno);

        return "redirect:/board/list";
    }

    @GetMapping({"/read", "/modify"})
    public void read(Long bno, PageRequestDto pageRequestDto, Model model){

        log.info("==========================================");
        log.info("여기 들어오고있씁니다 여긴 겟매핑 읽기,수정=====");
        log.info("==========================================");
        log.info(pageRequestDto);

        BoardDto boardDto = boardService.readOne(bno);

        log.info(boardDto);

        model.addAttribute("dto", boardDto);

    }
    @PreAuthorize("principal.username == #boardDto.writer")
    @PostMapping("/modify")
    public String modify(@Valid BoardDto boardDto,
                          BindingResult bindingResult,
                          PageRequestDto pageRequestDto,
                          RedirectAttributes redirectAttributes){

        if(bindingResult.hasErrors()) {
            log.info("has errors.......");

            String link = pageRequestDto.getLink();

            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors() );

            redirectAttributes.addAttribute("bno", boardDto.getBno());

            return "redirect:/board/modify?"+link;
        }

        boardService.modify(boardDto);

        redirectAttributes.addFlashAttribute("result", "modified");

        redirectAttributes.addAttribute("bno", boardDto.getBno());

        return "redirect:/board/read";
    }

    @PreAuthorize("principal.username == #boardDto.writer")
    @PostMapping("/remove")
    public String remove(BoardDto boardDto, RedirectAttributes redirectAttributes) {

        Long bno = boardDto.getBno();

        log.info("remove post.. " + bno);

        boardService.remove(bno);

        //게시물이 DB상에서 삭제되었을때 첨부파일 삭제
        log.info(boardDto.getFileNames());
        List<String> fileNames = boardDto.getFileNames();
        if(fileNames != null && fileNames.size() > 0){
            removeFiles(fileNames);
        }

        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/board/list";

    }

    public void removeFiles(List<String> files){

        for(String fileName : files){

            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

            String resourceName = resource.getFilename();

            try{
                String contentType = Files.probeContentType(resource.getFile().toPath());
                resource.getFile().delete();

                //섬네일이 존재하면
                if(contentType.startsWith("image")){
                    File thumbnailFile = new File(uploadPath + File.separator + fileName);
                    thumbnailFile.delete();
                }
            }   catch (Exception e){
                log.error(e.getMessage());
            }
        }
    }

}
