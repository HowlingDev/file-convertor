package com.example.converters;

public interface Convertible {

    boolean supports(String fileType);

    void convertToPdf(String fileName) throws Exception;
}
