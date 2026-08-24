package com.example.converters;

import lombok.Getter;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.io.ScratchFile;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
public class ZipToPdfConverter implements Converter {

    @Getter
    private final List<Converter> converters;

    public ZipToPdfConverter(List<Converter> allConverters) {
        this.converters = allConverters.stream()
                .filter(converter -> converter != this)
                .toList();
    }

    @Override
    public boolean supports(String fileType) {
        return fileType.equals("zip");
    }

    @Override
    public String convertToPdf(InputStream data, String fileName) {

        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        pdfMerger.setDocumentMergeMode(PDFMergerUtility.DocumentMergeMode.OPTIMIZE_RESOURCES_MODE);
        Path dir = Path.of("files/" + Converter.replaceExtension(fileName, "pdf"));
        pdfMerger.setDestinationFileName(dir.toString());
        List<String> files = new ArrayList<>();
        Path tmp = Path.of("files/tmp-" + fileName);
        try (InputStream inputStream = data) {
            Files.copy(inputStream, tmp, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        try (ZipFile zipFile = new ZipFile(tmp.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                try (InputStream entryStream = zipFile.getInputStream(entry)) {
                    int dotIndex = entry.getName().lastIndexOf(".");
                    String extension = entry.getName().substring(dotIndex + 1);
                    for (Converter converter : converters) {
                        if (converter.supports(extension)) {
                            String res = converter.convertToPdf(entryStream, entry.getName());
                            files.add(res);
                        }
                    }
                }
            }
            for (String file : files) {
                pdfMerger.addSource(file);
            }
            pdfMerger.mergeDocuments(() -> new ScratchFile(MemoryUsageSetting.setupMainMemoryOnly()));
            for (String file : files) {
                Files.deleteIfExists(Path.of(file));
            }

            return dir.toString();
        } catch (IOException e) {
            throw new RuntimeException();
        } finally {
            try {
                Files.deleteIfExists(tmp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}