package com.FTPPTF.mmganalytics.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.FTPPTF.mmganalytics.model.AnalyticsSnapshotEntity;

public interface AnalyticsSnapshotRepository extends JpaRepository<AnalyticsSnapshotEntity, UUID> {
}
