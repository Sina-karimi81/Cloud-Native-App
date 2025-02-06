package com.github.sinakarimi81.schedulerservice.objectStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
public class BucketService {

    @Value("${liara.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;

    public BucketService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadImage(MultipartFile file, String name) throws IOException {
        log.info("putting image with name: {} to object storage", name);
        Path tempFile = Files.createTempFile(null, name);
        file.transferTo(tempFile);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(name)
                .build();

        s3Client.putObject(putObjectRequest, tempFile);
        Files.delete(tempFile);
        log.info("finished putting image with name: {} to object storage", name);
    }
}
