package com.example;

import com.example.services.FileConversionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
@AllArgsConstructor
public class FileController {
    private FileConversionService fileConversionService;

    @GetMapping("/get/txt")
    public String getTxtFile() throws Exception {

        fileConversionService.convertTxt("testfile.txt");

        return "demobucket/testfile.pdf";
    }

    @GetMapping("/get/png")
    public String getPngFile() throws Exception {

        fileConversionService.convertPngImage("png-photo.png");

        return "demobucket/png-photo.pdf";
    }

    @GetMapping("/get/jpg")
    public String getJpgFile() throws Exception {

        fileConversionService.convertJpgImage("logo.jpeg");

        return "demobucket/logo.pdf";
    }

    @GetMapping("/get/zip")
    public String getZipFile() throws Exception {

        return fileConversionService.convertZip("testzip.zip");

//        return "demobucket/testzip.pdf";
    }
}
