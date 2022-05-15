package com.reggie.controller;

import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;
    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public R<String> load(MultipartFile file){
        String originalFileName=file.getOriginalFilename();
        log.info(originalFileName);
        String fileName= UUID.randomUUID().toString()+originalFileName.substring(originalFileName.lastIndexOf("."));
        /**
         * 创建目录
         */
        File dir=new File(basePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        /**
         * 将临时文件转存到指定位置
         */
        try{
            file.transferTo(new File(basePath+fileName));
        }catch (Exception e){

        }

        return R.success(fileName);
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //输入流读取文件
            FileInputStream fileInputStream=new FileInputStream(new File(basePath+name));
            //输出流返回文件
            ServletOutputStream outputStream=response.getOutputStream();
            response.setContentType("image/jpeg");
            int len=0;
            byte[] bytes=new byte[1024];
            while((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
