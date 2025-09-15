package com.talentstream.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "video_metadata")
public class VideoMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "video_id") 
    private Long videoId;

    private String title;
    private String tags; 
    private String s3Url;
    
	public VideoMetadata( String title, String tags, String s3Url) {
		this.title = title;
		this.tags = tags;
		this.s3Url = s3Url;
	}
	public VideoMetadata() {
		
	}
	
	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}
	
	public Long getVideoId() {
		return videoId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getS3Url() {
		return s3Url;
	}
	public void setS3Url(String s3Url) {
		this.s3Url = s3Url;
	}
}

