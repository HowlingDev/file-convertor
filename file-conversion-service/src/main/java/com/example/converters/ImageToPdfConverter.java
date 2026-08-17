package com.example.converters;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Component
public class ImageToPdfConverter extends Converter implements Convertible {

    @Override
    public boolean supports(String fileType) {
        return fileType.equals("jpg") || fileType.equals("png");
    }

    @Override
    public void convertToPdf(String fileName) throws Exception {

        super.createBucketIfNotExists();

        String newFileName = super.replaceExtension(fileName, ".pdf");

        try (InputStream inputStream = super.download(fileName);
             PDDocument doc = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            byte[] imageBytes = inputStream.readAllBytes();
            PDPage myPage = new PDPage();
            doc.addPage(myPage);
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, imageBytes, newFileName);

            try (PDPageContentStream cont = new PDPageContentStream(doc, myPage)) {
                cont.drawImage(pdImage, 20, 20, pdImage.getWidth(), pdImage.getHeight());
            }

            doc.save(out);
            super.upload(out.toByteArray(), newFileName);
        }
    }
}
