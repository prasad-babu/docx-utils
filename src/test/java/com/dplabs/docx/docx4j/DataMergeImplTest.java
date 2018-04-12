package com.dplabs.docx.docx4j;

import com.dplabs.file.FileRepository;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class DataMergeImplTest {

    private DataMergeImpl dataMerge;

    @Test
    public void testReplacePlaceHolders() throws IOException, Docx4JException {
        FileRepository repository = new FileRepositoryImpl();
        try(InputStream inputStream = repository.read("DocumentTemplate.docx");
            OutputStream outputStream = repository.getOutputStream("Document_result.docx")){
            dataMerge = new DataMergeImpl(repository, inputStream);
            Map<String, String> placeHolders = new HashMap<>();
            placeHolders.put("${webdavId.100}", "100.docx");
            placeHolders.put("${webdavId.101}", "101.docx");
            dataMerge.replacePlaceHoldersWithDocx(placeHolders);

            placeHolders = new HashMap<>();
            String html = "";
            try (InputStream input = repository.read("100.html");
                 InputStreamReader reader = new InputStreamReader(input);
                 BufferedReader buffer = new BufferedReader(reader)) {
                html = buffer.lines().collect(Collectors.joining("\n"));
            }
            placeHolders.put("${html.100}", html);

            try (InputStream input = repository.read("101.html");
                 InputStreamReader reader = new InputStreamReader(input);
                 BufferedReader buffer = new BufferedReader(reader)) {
                html = buffer.lines().collect(Collectors.joining("\n"));
            }
            placeHolders.put("${html.101}", html);

            dataMerge.replacePlaceHolderWithHtml(placeHolders);

            dataMerge.save(outputStream);
        }
    }

    static class FileRepositoryImpl implements FileRepository {

        @Override
        public InputStream read(String _fileName) throws IOException {
            return getClass().getClassLoader().getResourceAsStream(_fileName);
        }

        @Override
        public OutputStream getOutputStream(String _fileName) throws IOException {
            return Files.newOutputStream(Paths.get(_fileName));
        }
    }

}
