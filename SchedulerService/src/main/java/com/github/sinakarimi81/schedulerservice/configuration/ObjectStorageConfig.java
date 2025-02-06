package com.github.sinakarimi81.schedulerservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class ObjectStorageConfig {

    @Value("${liara.access.key}")
    private String accessKey;
    @Value("${liara.secret.key}")
    private String secretKey;
    @Value("${liara.objstore.endpoint}")
    private String bucketEndpoint;

    @Bean
    public S3Client getS3Client() {
        return S3Client.builder()
                .region(Region.of("us-west-2"))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .endpointOverride(URI.create(bucketEndpoint))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

}
