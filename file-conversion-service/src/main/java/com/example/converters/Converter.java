package com.example.converters;

import java.io.InputStream;

public interface Converter {

    boolean supports(String fileType);

    void convertToPdf(InputStream data, String fileName) throws Exception;
}
