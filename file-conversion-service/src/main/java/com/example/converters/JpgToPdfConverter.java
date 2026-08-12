package com.example.converters;

import org.springframework.stereotype.Component;

@Component
public class JpgToPdfConverter implements Converter {

    @Override
    public boolean supports(String fileType) {
        return fileType.equals("jpg");
    }

    @Override
    public void convertToPdf() {

    }
}
