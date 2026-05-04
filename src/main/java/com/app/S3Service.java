package com.app;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

public class S3Service {

    private final String BUCKET_NAME = "cc-lambda-s32dynamodb";
    private final Region region = Region.US_EAST_1;

    public void uploadFile(String fileName, InputStream inputStream, long fileSize) {

        System.out.println("🌍 Creating S3 client...");

        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        System.out.println("📦 Uploading to bucket: " + BUCKET_NAME);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(fileName)
                .build();

        s3.putObject(request, RequestBody.fromInputStream(inputStream, fileSize));

        System.out.println("🎉 File uploaded to S3!");
    }
}