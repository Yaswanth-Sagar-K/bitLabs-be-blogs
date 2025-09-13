package com.talentstream.dto;

import java.util.UUID;

public class ApplicantWatchDTO {

    private Integer applicantId;
    private Long videoId;

   
    public Integer getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Integer applicantId) {
        this.applicantId = applicantId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }
}
