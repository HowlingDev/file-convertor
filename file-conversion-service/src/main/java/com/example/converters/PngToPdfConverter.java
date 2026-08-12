package com.example.converters;

import org.springframework.stereotype.Component;

@Component
public class PngToPdfConverter implements Converter {

    @Override
    public boolean supports(String fileType) {
        return fileType.equals("png");
    }

    @Override
    public void convertToPdf() {

    }
}
