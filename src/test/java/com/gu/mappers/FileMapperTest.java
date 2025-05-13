package com.gu.mappers;

import com.gu.persistence.SnapshotFile;
import com.gu.persistence.SnapshotFileContent;
import com.gu.repositories.FileContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileMapperTest {

    @Mock
    private FileContentRepository fileContentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMapFilesToSnapshotFiles_NewFileContent() throws Exception {
        List<File> files = new ArrayList<>();
        File mockFile = mock(File.class);
        when(mockFile.getName()).thenReturn("testFile.txt");
        when(mockFile.getPath()).thenReturn("testDir/testFile.txt");
        files.add(mockFile);

        List<SnapshotFile> previousSnapshotFiles = new ArrayList<>();
        when(fileContentRepository.save(any(SnapshotFileContent.class)))
                .thenAnswer(invocation -> {
                    SnapshotFileContent sfc = invocation.getArgument(0);
                    sfc.setId(1);
                    return sfc;
                });

        List<SnapshotFile> result = FileMapper.mapFilesToSnapshotFiles(
                files, 1, "testDir", previousSnapshotFiles, fileContentRepository);

        assertEquals(1, result.size());
        SnapshotFile snapshotFile = result.get(0);
        assertEquals("testFile.txt", snapshotFile.getFileName());
        assertEquals("testFile.txt", snapshotFile.getPath());
        assertNotNull(snapshotFile.getFileContentId());
        verify(fileContentRepository, times(1)).save(any(SnapshotFileContent.class));
    }

    @Test
    void testConvertFileToByteArray() {
        File mockFile = mock(File.class);
        when(mockFile.getPath()).thenReturn("testFile.txt");

        byte[] result = FileMapper.convertFileToByteArray("testFile.txt");
        assertNull(result); // Since no actual file exists
    }

    @Test
    void testSameContent() {
        List<SnapshotFile> previousSnapshotFiles = new ArrayList<>();
        SnapshotFile snapshotFile = new SnapshotFile();
        snapshotFile.setContentHash("testHash");
        previousSnapshotFiles.add(snapshotFile);

        boolean result = FileMapper.sameContent("testHash", previousSnapshotFiles);
        assertTrue(result);

        result = FileMapper.sameContent("differentHash", previousSnapshotFiles);
        assertFalse(result);
    }

    @Test
    void testFindMatchingFileContentId() {
        List<SnapshotFile> previousSnapshotFiles = new ArrayList<>();
        SnapshotFile snapshotFile = new SnapshotFile();
        snapshotFile.setFileName("testFile.txt");
        snapshotFile.setPath("testPath");
        snapshotFile.setFileContentId(1);
        previousSnapshotFiles.add(snapshotFile);

        SnapshotFile currentFile = new SnapshotFile();
        currentFile.setFileName("testFile.txt");
        currentFile.setPath("testPath");

        Integer result = FileMapper.findMatchingFileContentId(previousSnapshotFiles, currentFile);
        assertEquals(1, result);

        currentFile.setPath("differentPath");
        result = FileMapper.findMatchingFileContentId(previousSnapshotFiles, currentFile);
        assertEquals(-1, result);
    }
}