package com.talentstream.dto;

import java.util.UUID;

public class VideoMetadataDto {
    private Long videoId;
    private String s3url;
    private String tags;
    private String title;

    public VideoMetadataDto(Long videoId, String s3url, String tags, String title) {
        this.videoId = videoId;
        this.s3url = s3url;
        this.tags = tags;
        this.title = title;
    }

	public Long getVideoId() {
		return videoId;
	}

	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}

	public String getS3url() {
		return s3url;
	}

	public void setS3url(String s3url) {
		this.s3url = s3url;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    // Getters and Setters
    
}
