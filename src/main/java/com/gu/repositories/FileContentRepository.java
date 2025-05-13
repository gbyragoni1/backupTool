package com.gu.repositories;

import com.gu.persistence.SnapshotFileContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileContentRepository extends JpaRepository<SnapshotFileContent, Integer> {
}
