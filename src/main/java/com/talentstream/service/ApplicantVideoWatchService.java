package com.talentstream.service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.talentstream.entity.ApplicantVideoWatchHistory;
import com.talentstream.repository.ApplicantVideoWatchHistoryRepository;

@Service
public class ApplicantVideoWatchService {

    @Autowired
    private ApplicantVideoWatchHistoryRepository repository;

    public void saveOrUpdateWatchHistory(Integer applicantId, Long videoId) {
        Optional<ApplicantVideoWatchHistory> optionalHistory = repository.findByApplicantIdAndVideoId(applicantId, videoId);

        if (optionalHistory.isPresent()) {
            ApplicantVideoWatchHistory history = optionalHistory.get();
            Integer currentCount = history.getViewCount() != null ? history.getViewCount() : 0;
            history.setViewCount(currentCount + 1);
            history.setWatchedAt(LocalDateTime.now());
            repository.save(history);
        } else {
            ApplicantVideoWatchHistory newHistory = new ApplicantVideoWatchHistory();
            newHistory.setApplicantId(applicantId);
            newHistory.setVideoId(videoId);
            newHistory.setWatchedAt(LocalDateTime.now());
            newHistory.setViewCount(1);
            repository.save(newHistory);
        }
    }
}
