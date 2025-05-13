package com.gu;

import com.gu.persistence.Snapshot;
import com.gu.services.BackupService;
import com.gu.services.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BackupToolTest {

    private BackupService backupService;
    private FileService fileService;

    @BeforeEach
    void setUp() {
        backupService = mock(BackupService.class);
        fileService = mock(FileService.class);
    }

    @Test
    void testSnapshot() {
        String[] args = {"snapshot", "--target-directory=./testDir"};
        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.isDirectory()).thenReturn(true);

        doAnswer(invocation -> {
            List<File> files = invocation.getArgument(0);
            files.add(mockFile);
            return null;
        }).when(fileService).collectFiles(anyList(), any(File.class));

        BackupTool.snapshot(args, backupService, fileService);

        verify(fileService, times(1)).collectFiles(anyList(), any(File.class));
        verify(backupService, times(1)).createSnapshot(anyList(), any(String.class));
    }

    @Test
    void testList() {
        Snapshot snapshot = new Snapshot();
        snapshot.setId(1);
        snapshot.setCreateTime(Timestamp.from(new Date().toInstant()));
        when(backupService.listSnapshots()).thenReturn(Collections.singletonList(snapshot));

        BackupTool.list(backupService);

        verify(backupService, times(1)).listSnapshots();
    }

    @Test
    void testPrune() {
        String[] args = {"prune", "--snapshot=1"};

        BackupTool.prune(args, backupService);

        verify(backupService, times(1)).pruneSnapshot(1);
    }

    @Test
    void testFixTargetDirectory() {
        String home = System.getProperty("user.home");
        String cwd = System.getProperty("user.dir");

        assertEquals(home + "/testDir", BackupTool.fixTargetDirectory("~/testDir"));
        assertEquals(cwd + "/testDir", BackupTool.fixTargetDirectory("./testDir"));
        assertEquals("/absolute/path", BackupTool.fixTargetDirectory("/absolute/path"));
    }
}