package com.talentstream.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talentstream.entity.ApplicantVideoWatchHistory;

public interface ApplicantVideoWatchHistoryRepository extends JpaRepository<ApplicantVideoWatchHistory, Long> {

    Optional<ApplicantVideoWatchHistory> findByApplicantIdAndVideoId(Integer applicantId, Long videoId);
}
