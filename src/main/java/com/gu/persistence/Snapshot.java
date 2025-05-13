package com.gu.persistence;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@SequenceGenerator(name = "snapshot_sequence", sequenceName = "snapshot_sequence", initialValue = 1, allocationSize = 1)
public class Snapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "snapshot_sequence")
    private Integer id;
    private String directory;

    @Column(name = "create_date")
    private Timestamp createTime;

    public Snapshot(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
