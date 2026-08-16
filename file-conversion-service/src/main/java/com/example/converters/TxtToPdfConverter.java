package com.example.converters;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Component
public class TxtToPdfConverter extends Converter {

    @Override
    public boolean supports(String fileType) {
        return fileType.equals("txt");
    }

    @Override
    public void convertToPdf(String fileName) throws Exception {

        super.createBucketIfNotExists();

        String newFileName = super.replaceExtension(fileName, ".pdf");

        try (InputStream inputStream = super.download(fileName)) {
            byte[] bytes = inputStream.readAllBytes();
            try (PDDocument doc = Loader.loadPDF(bytes);
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                doc.save(out);
                super.upload(out.toByteArray(), newFileName);
            }
        }
    }

}
