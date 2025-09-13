package com.talentstream.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.ApplicantWatchDTO;
import com.talentstream.service.ApplicantVideoWatchService;

@RestController
@RequestMapping("/api/video-watch")
public class ApplicantVideoWatchController {

    @Autowired
    private ApplicantVideoWatchService watchService;

    @PostMapping("/track")
    public ResponseEntity<String> trackWatch(@RequestBody ApplicantWatchDTO dto) {
        watchService.saveOrUpdateWatchHistory(dto.getApplicantId(), dto.getVideoId());
        return ResponseEntity.ok("Watch history updated");
    }
}
