package com.gu.persistence;

import jakarta.persistence.*;

@Entity
@SequenceGenerator(name = "snapshot_file_sequence", sequenceName = "snapshot_file_sequence", initialValue = 1, allocationSize = 1)
public class SnapshotFile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "snapshot_file_sequence")
    private Integer id;
    private Integer snapshotId;
    private String fileName;
    private String path;
    private String contentHash;
    private Integer fileContentId;

    public SnapshotFile(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(Integer snapshotId) {
        this.snapshotId = snapshotId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public Integer getFileContentId() {
        return fileContentId;
    }

    public void setFileContentId(Integer fileContentId) {
        this.fileContentId = fileContentId;
    }
}
