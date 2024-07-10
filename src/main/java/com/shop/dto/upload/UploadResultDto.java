package com.shop.dto.upload;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class UploadResultDto {

    private String uuid;

    private String fileName;

    private boolean img;

    public String getLink(){

        if(img){
            return "s_" + uuid + "_" + fileName; // 이미지인경우 썸네일
        }else{
            return uuid + "_" + fileName;
        }
    }
}
