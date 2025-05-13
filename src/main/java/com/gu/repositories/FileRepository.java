package com.gu.repositories;

import com.gu.persistence.SnapshotFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<SnapshotFile, Integer> {
    List<SnapshotFile> findBySnapshotId(Integer id);

    List<SnapshotFile> findByFileContentId(Integer fileContentId);
}
