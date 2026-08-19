package com.example.converters;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class TxtToPdfConverter implements Converter {

    private static final float MARGIN_LEFT = 50f;
    private static final float MARGIN_TOP = 72f;
    private static final float MARGIN_BOTTOM = 50f;
    private static final float FONT_SIZE = 12f;
    private static final float LEADING = 14.5f;

    @Override
    public boolean supports(String fileType) {
        return fileType.equals("txt");
    }

    @Override
    public String convertToPdf(InputStream data, String fileName) {

        String line;

        try (BufferedReader bf = new BufferedReader(new InputStreamReader(data, StandardCharsets.UTF_8));
             PDDocument doc = new PDDocument()) {

            var font = PDType0Font.load(doc,
                    new FileInputStream("C:/Windows/Fonts/arial.ttf"));
            PDPage currentPage = null;
            PDPageContentStream contentStream = null;
            float yPosition = 0f;

            while ((line = bf.readLine()) != null) {
                List<String> splittedLines = splitLineByWidth(line, avgCharsInLine(font));
                for (String text : splittedLines) {
                    if (currentPage == null || yPosition < MARGIN_BOTTOM) {
                        if (contentStream != null) {
                            contentStream.endText();
                            contentStream.close();
                        }
                        currentPage = new PDPage(PDRectangle.A4);
                        doc.addPage(currentPage);

                        contentStream = new PDPageContentStream(doc, currentPage);
                        contentStream.beginText();
                        contentStream.setFont(font, FONT_SIZE);
                        contentStream.setLeading(LEADING);
                        yPosition = currentPage.getMediaBox().getHeight() - MARGIN_TOP;
                        contentStream.newLineAtOffset(MARGIN_LEFT, yPosition);
                    } else {
                        contentStream.newLine();
                        yPosition -= LEADING;
                    }

                    contentStream.showText(text);
                }
            }

            if (contentStream != null) {
                contentStream.endText();
                contentStream.close();
            }

            String dir = "C:/Users/user/Desktop/" + Converter.replaceExtension(fileName, "pdf");
            doc.save(new File(dir));
            return dir;
        } catch (IOException e) {
            return "";
        }
    }

    private List<String> splitLineByWidth(String line, int width) {
        if (line == null || width <= 0) {
            return new ArrayList<>();
        }
        return IntStream.iterate(0, i -> i < line.length(), i -> i + width)
                .mapToObj(i -> line.substring(i, Math.min(i + width, line.length())))
                .toList();
    }



    private int avgCharsInLine(PDFont font) throws IOException {
        float availableWidth = PDRectangle.A4.getWidth() - MARGIN_LEFT;
        float charWidth = font.getStringWidth("a") / 1000f * FONT_SIZE;
        return Math.round(availableWidth / charWidth * 0.97f);
    }
}