package com.example.converters;

import io.minio.MinioClient;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ZipToPdfConverter implements Convertible {

    @Value("${minio.bucket}")
    private String bucket;

    private MinioClient minioClient;

    List<Converter> converters;

    @Override
    public boolean supports(String fileType) {
        return fileType.equals("zip");
    }

    @Override
    public void convertToPdf(String fileName) throws Exception {
    }
}
