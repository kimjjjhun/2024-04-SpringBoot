package com.shop.controller;


import com.shop.dto.upload.UploadFileDto;
import com.shop.dto.upload.UploadResultDto;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Log4j2
public class UploadController {

    //value 어노테이션은 애플리케이션프로퍼티스 설정정보를 읽어서 변수의 값으로 사용할수있게 해줍니다.
    @Value("${com.shop.upload.path}")// import 시에 springframework으로 시작하는 value
    private String uploadPath;
    //첨부파일 업로드
    @ApiOperation(value = "Upload POST", notes = "POST 방식으로 파일 등록")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDto> upload(UploadFileDto uploadFileDto){

        log.info(uploadFileDto);

        if(uploadFileDto.getFiles() != null){

            final List<UploadResultDto> list = new ArrayList<>();

            uploadFileDto.getFiles().forEach(multipartFile -> {

                String originalFileName = multipartFile.getOriginalFilename();

                log.info(originalFileName);

                String uuid = UUID.randomUUID().toString();

                Path savePath = Paths.get(uploadPath, uuid+"_"+ originalFileName);

                boolean image = false;

                try{
                    multipartFile.transferTo(savePath); // 실제 파일 저장

                    //이미지 파일의 종류라면
                    if(Files.probeContentType(savePath).startsWith("image")) {

                        image = true;

                        File thumbfile = new File(uploadPath, "s_" + uuid + "_" + originalFileName);

                        Thumbnailator.createThumbnail(savePath.toFile(), thumbfile, 200, 200);
                    }
                } catch (Exception e){

                    e.printStackTrace();
                }

                list.add(UploadResultDto.builder()
                                .uuid(uuid)
                                .fileName(originalFileName)
                                .img(image).build());

            }); // end eath

            return list;

        } // end if

        return null;
    }
    //첨부파일 조회
    @ApiOperation(value = "view 파일" , notes = "GET방식으로 첨부파일 조회")
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName){

        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        String resourceName = resource.getFilename();
        HttpHeaders headers = new HttpHeaders();

        try{
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @ApiOperation(value = "remove 파일", notes = "DELETE 방식으로 파일 삭제")
    @DeleteMapping("/remove/{fileName}")
    public Map<String,Boolean> removeFile(@PathVariable String fileName){

        Resource resource = new FileSystemResource(uploadPath+File.separator + fileName);
        String resourceName = resource.getFilename();

        Map<String, Boolean> resultMap = new HashMap<>();
        boolean removed = false;

        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            removed = resource.getFile().delete();

            //섬네일이 존재한다면
            if(contentType.startsWith("image")){
                File thumbnailFile = new File(uploadPath+File.separator +"s_" + fileName);
                thumbnailFile.delete();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        resultMap.put("result", removed);

        return resultMap;
    }
}
