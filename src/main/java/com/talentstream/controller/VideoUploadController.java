package com.talentstream.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.talentstream.dto.VideoMetadataDto;
import com.talentstream.entity.VideoMetadata;
import com.talentstream.repository.VideoMetadataRepository;
import com.talentstream.service.S3Service;
import com.talentstream.service.VideoService;

@RestController
@RequestMapping("/videos")
public class VideoUploadController {

    @Autowired 
    private S3Service s3Service;
    @Autowired 
    private VideoMetadataRepository repo;
    
    
    @Autowired
    private VideoService videoService;


    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadVideo(
            @RequestPart("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("tags") String tags
    ) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is required and cannot be empty.");
        }

        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Title is required.");
        }

        if (tags == null || tags.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tags are required.");
        }

        try {
            String s3Url = s3Service.uploadFile(file);
            VideoMetadata video = new VideoMetadata(title, tags, s3Url);
            repo.save(video);
            return ResponseEntity.ok("Video uploaded successfully with metadata."+s3Url);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }

    }
    
//    @GetMapping("/by-applicant/{applicantId}")
//    public ResponseEntity<List<VideoProjection>> getVideosByApplicant(@PathVariable Integer applicantId) {
//        List<VideoProjection> videos = repo.findMatchedAndUnmatchedVideos(applicantId);
//        return ResponseEntity.ok(videos);
//    }

 
    @GetMapping("/recommended/{applicantId}")
    public ResponseEntity<List<VideoMetadataDto>> getRecommended(@PathVariable Integer applicantId) {
        List<VideoMetadataDto> videos = videoService.getRecommendedVideos(applicantId);
        return ResponseEntity.ok(videos);
}
}

