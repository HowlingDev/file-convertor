package com.example.converters;

import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class ZipToPdfConverter implements Converter {

    @Override
    public boolean supports(String fileType) {
        return fileType.equals("zip");
    }

    @Override
    public void convertToPdf(InputStream data, String fileName) throws Exception {
    }
}
