package com.example.converters;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class ImageToPdfConverter implements Converter {

    private final List<String> supportedFormats = List.of("jpg", "jpeg", "jpe", "jfif", "png");

    @Override
    public boolean supports(String fileType) {
        return supportedFormats.contains(fileType);
    }

    @Override
    public String convertToPdf(InputStream data, String fileName) {

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDImageXObject pdImage = LosslessFactory.createFromImage(doc, ImageIO.read(data));
            double scale = Math.min((double) page.getMediaBox().getWidth() / pdImage.getWidth(),
                    (double) page.getMediaBox().getHeight() / pdImage.getHeight());
            try (PDPageContentStream cont = new PDPageContentStream(doc, page)) {
                cont.drawImage(pdImage, 20, 20, (int) (pdImage.getWidth() * scale),
                        (int) (pdImage.getHeight() * scale));
            }
            Path dir = Path.of("files/" + Converter.replaceExtension(fileName, "pdf"));
            Files.createDirectories(Path.of("files/"));
            doc.save(dir.toFile());
            return dir.toString();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
