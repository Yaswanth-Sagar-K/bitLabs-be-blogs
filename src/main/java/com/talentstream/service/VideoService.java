package com.talentstream.service;

import com.talentstream.dto.VideoMetadataDto;
import com.talentstream.repository.VideoMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VideoService {

    @Autowired
    private VideoMetadataRepository videoMetadataRepository;

    public List<VideoMetadataDto> getRecommendedVideos(Integer applicantId) {
    
        List<Object[]> rawData = videoMetadataRepository.fetchRecommendedVideos(applicantId);

        return rawData.stream()
            .map(obj -> new VideoMetadataDto(
                obj[0] != null ? Long.valueOf(obj[0].toString()) : null, // video_id
                obj[1] != null ? obj[1].toString() : "", // s3_url
                obj[2] != null ? obj[2].toString() : "", // tags
                obj[3] != null ? obj[3].toString() : ""  // title
            ))
            .toList();
    }

}
