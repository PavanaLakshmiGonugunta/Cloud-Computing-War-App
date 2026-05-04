package com.app;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.InputStream;

public class S3Service {
    private final String BUCKET_NAME = "your-bucket-name-here";
    private final Region region = Region.US_EAST_1; // Change to your region

    public void uploadFile(String fileName, InputStream inputStream, long fileSize) {
        S3Client s3 = S3Client.builder().region(region).build();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(fileName)
                .build();

        s3.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, fileSize));
    }
}