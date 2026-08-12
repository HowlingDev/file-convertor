package com.example.converters;

public interface Converter {

    boolean supports(String fileType);

    void convertToPdf();
}
