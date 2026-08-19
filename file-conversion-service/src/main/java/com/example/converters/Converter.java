package com.example.converters;

import java.io.InputStream;

public interface Converter {

    boolean supports(String fileType);

    String convertToPdf(InputStream data, String fileName);

    static String replaceExtension(String fileName, String extension) {
        int dotIndex = fileName.lastIndexOf(".");
        return fileName.substring(0, dotIndex + 1) + extension;
    }
}
