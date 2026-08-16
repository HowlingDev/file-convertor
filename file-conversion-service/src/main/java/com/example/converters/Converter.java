package com.example.converters;

import io.minio.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
@AllArgsConstructor
@NoArgsConstructor
public abstract class Converter {

    @Value("${minio.bucket}")
    private String bucket;

    private MinioClient minioClient;

    public abstract boolean supports(String fileType);

    public abstract void convertToPdf(String fileName) throws Exception;

    public InputStream download(String fileName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(fileName)
                        .build());
    }

    public void upload(byte[] bytes, String fileName) throws Exception {

        try (InputStream in = new ByteArrayInputStream(bytes)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(in, bytes.length, -1)
                            .contentType("application/pdf")
                            .build());
        }
    }

    public void createBucketIfNotExists() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    public String replaceExtension(String fileName, String newExtension) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1) {
            return fileName.substring(0, dotIndex) + newExtension;
        }
        return fileName + newExtension;
    }
}
