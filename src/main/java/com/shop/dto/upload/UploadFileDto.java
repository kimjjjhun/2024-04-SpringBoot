package com.shop.dto.upload;


import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Log4j2
public class UploadFileDto {

    private List<MultipartFile> files;
}
