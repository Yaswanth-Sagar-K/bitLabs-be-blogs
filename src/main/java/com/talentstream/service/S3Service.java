package com.talentstream.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${application.bucket.name}")
    private String bucketName;

    public S3Service(
            @Value("${cloud.aws.credentials.access-key}") String accessKey,
            @Value("${cloud.aws.credentials.secret-key}") String secretKey,
            @Value("${cloud.aws.region.static}") String region) {

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
    public String uploadFile(MultipartFile file) throws IOException {
  
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }


        String uniqueFileName = UUID.randomUUID().toString() + extension;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                    .build();

            // ✅ Upload file
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // ✅ Return public HTTPS URL
            String region = s3Client.serviceClientConfiguration().region().id();
            return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + uniqueFileName;

        } catch (S3Exception e) {
            throw new RuntimeException("Failed to upload file to S3: " + e.awsErrorDetails().errorMessage(), e);
        }
    }
}
