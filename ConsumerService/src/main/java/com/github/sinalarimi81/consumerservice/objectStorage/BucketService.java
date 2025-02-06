package com.github.sinalarimi81.consumerservice.objectStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class BucketService {

    @Value("${liara.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;

    public BucketService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public byte[] downloadImage(String fileName) throws IOException {
        log.info("getting image with name: {} to object storage", fileName);
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        File file = new File(fileName);
        s3Client.getObject(getObjectRequest, Paths.get(file.getPath()));
        log.info("finished getting image with name: {} to object storage", fileName);
        return Files.readAllBytes(file.toPath());
    }
}
