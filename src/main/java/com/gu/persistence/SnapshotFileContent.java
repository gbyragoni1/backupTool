package com.gu.persistence;

import jakarta.persistence.*;

import java.sql.Blob;

@Entity
@SequenceGenerator(name = "snapshot_file_content_sequence", sequenceName = "snapshot_file_content_sequence", initialValue = 1, allocationSize = 1)
public class SnapshotFileContent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "snapshot_file_content_sequence")
    private Integer id;
    private String fileName;
    private Blob fileContent;

    public SnapshotFileContent(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Blob getFileContent() {
        return fileContent;
    }

    public void setFileContent(Blob fileContent) {
        this.fileContent = fileContent;
    }

}
