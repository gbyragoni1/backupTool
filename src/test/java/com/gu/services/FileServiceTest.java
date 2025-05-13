package com.gu.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FileServiceTest {

    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileService();
    }

    @Test
    void testCollectFilesWithSingleFile() {
        File mockFile = mock(File.class);
        when(mockFile.isDirectory()).thenReturn(false);

        List<File> files = new ArrayList<>();
        List<File> result = fileService.collectFiles(files, mockFile);

        assertEquals(1, result.size());
        assertEquals(mockFile, result.get(0));
    }

    @Test
    void testCollectFilesWithDirectory() {
        File mockDirectory = mock(File.class);
        File mockFile1 = mock(File.class);
        File mockFile2 = mock(File.class);

        when(mockDirectory.isDirectory()).thenReturn(true);
        when(mockDirectory.listFiles()).thenReturn(new File[]{mockFile1, mockFile2});
        when(mockFile1.isDirectory()).thenReturn(false);
        when(mockFile2.isDirectory()).thenReturn(false);

        List<File> files = new ArrayList<>();
        List<File> result = fileService.collectFiles(files, mockDirectory);

        assertEquals(2, result.size());
        assertEquals(mockFile1, result.get(0));
        assertEquals(mockFile2, result.get(1));
    }

    @Test
    void testCollectFilesWithNestedDirectories() {
        File mockRoot = mock(File.class);
        File mockSubDir = mock(File.class);
        File mockFile = mock(File.class);

        when(mockRoot.isDirectory()).thenReturn(true);
        when(mockRoot.listFiles()).thenReturn(new File[]{mockSubDir});
        when(mockSubDir.isDirectory()).thenReturn(true);
        when(mockSubDir.listFiles()).thenReturn(new File[]{mockFile});
        when(mockFile.isDirectory()).thenReturn(false);

        List<File> files = new ArrayList<>();
        List<File> result = fileService.collectFiles(files, mockRoot);

        assertEquals(1, result.size());
        assertEquals(mockFile, result.get(0));
    }
}