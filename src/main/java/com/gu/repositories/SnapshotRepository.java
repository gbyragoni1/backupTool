package com.gu.repositories;

import com.gu.persistence.Snapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnapshotRepository extends JpaRepository<Snapshot, Integer> {
    @Override
    @Query("select s from Snapshot s order by id")
    List<Snapshot> findAll();

    @Query("select s from Snapshot s where id=(select max(id) from Snapshot) ")
    Snapshot findLatest();
}
