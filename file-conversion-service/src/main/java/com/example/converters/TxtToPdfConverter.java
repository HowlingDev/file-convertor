package com.example.converters;

import org.springframework.stereotype.Component;

@Component
public class TxtToPdfConverter implements Converter {

    @Override
    public boolean supports(String fileType) {
        return fileType.equals("txt");
    }

    @Override
    public void convertToPdf() {

    }

}
