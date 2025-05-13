package com.gu.services;

import com.gu.mappers.FileMapper;
import com.gu.persistence.Snapshot;
import com.gu.persistence.SnapshotFile;
import com.gu.persistence.SnapshotFileContent;
import com.gu.repositories.FileContentRepository;
import com.gu.repositories.FileRepository;
import com.gu.repositories.SnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.sql.Blob;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BackupServiceTest {
    @Mock
    private SnapshotRepository snapshotRepository;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private FileContentRepository fileContentRepository;

    @Mock
    private FileMapper fileMapper;

    @InjectMocks
    private BackupService backupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListSnapshots() {
        List<Snapshot> snapshots = new ArrayList<>();
        when(snapshotRepository.findAll()).thenReturn(snapshots);

        List<Snapshot> result = backupService.listSnapshots();

        assertEquals(snapshots, result);
        verify(snapshotRepository, times(1)).findAll();
    }

    @Test
    void testPruneSnapshot() {
        Integer snapshotId = 1;
        List<SnapshotFile> snapshotFiles = new ArrayList<>();
        List<SnapshotFileContent> snapshotFileContents = new ArrayList<>();
        SnapshotFile snapshotFile = new SnapshotFile();
        snapshotFile.setFileContentId(1);
        snapshotFiles.add(snapshotFile);

        SnapshotFileContent snapshotFileContent = new SnapshotFileContent();
        snapshotFileContent.setId(1);
        snapshotFileContents.add(snapshotFileContent);

        when(fileRepository.findBySnapshotId(snapshotId)).thenReturn(snapshotFiles);
        when(fileContentRepository.findById(1)).thenReturn(Optional.of(snapshotFileContent));
        when(fileRepository.findByFileContentId(1)).thenReturn(snapshotFiles);

        backupService.pruneSnapshot(snapshotId);

        verify(fileContentRepository, times(1)).deleteAll(snapshotFileContents);
        verify(fileRepository, times(1)).deleteAll(snapshotFiles);
        verify(snapshotRepository, times(1)).deleteById(snapshotId);
    }

    @Test
    void testRestoreSnapshot() throws Exception {
        Integer snapshotId = 1;
        String targetDirectory = "testDir";
        List<SnapshotFile> snapshotFiles = new ArrayList<>();
        SnapshotFile snapshotFile = new SnapshotFile();
        snapshotFile.setPath("testFile.txt");
        snapshotFile.setFileContentId(1);
        snapshotFiles.add(snapshotFile);

        SnapshotFileContent snapshotFileContent = mock(SnapshotFileContent.class);
        Blob blob = mock(Blob.class);
        when(blob.getBytes(1, (int) blob.length())).thenReturn(new byte[]{});
        when(snapshotFileContent.getFileContent()).thenReturn(blob);

        when(fileRepository.findBySnapshotId(snapshotId)).thenReturn(snapshotFiles);
        when(fileContentRepository.findById(1)).thenReturn(Optional.of(snapshotFileContent));

        backupService.restoreSnapshot(snapshotId, targetDirectory);

        verify(fileRepository, times(1)).findBySnapshotId(snapshotId);
        verify(fileContentRepository, times(1)).findById(1);
    }
}
