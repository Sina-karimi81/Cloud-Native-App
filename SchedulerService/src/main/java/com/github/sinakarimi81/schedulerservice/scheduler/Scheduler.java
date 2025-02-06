package com.github.sinakarimi81.schedulerservice.scheduler;

import com.github.sinakarimi81.schedulerservice.common.RequestStatus;
import com.github.sinakarimi81.schedulerservice.database.RequestDAO;
import com.github.sinakarimi81.schedulerservice.database.RequestEntity;
import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.List;

@Service
@Slf4j
public class Scheduler {

    @Value("${hugging.face.image-generator.api}")
    private String apiUrl;
    @Value("${hugging.face.api.key}")
    private String apiKey;
    @Value("${liara.bucket.name}")
    private String bucketName;
    @Value("${liara.objstore.endpoint}")
    private String bucketEndpoint;
    @Value("${mail.api.token}")
    private String mailToken;

    private final RequestDAO requestDAO;
    private final S3Client s3Client;

    public Scheduler(RequestDAO requestDAO, S3Client s3Client) {
        this.requestDAO = requestDAO;
        this.s3Client = s3Client;
    }


    @Scheduled(fixedDelay = 30000)
    public void captionToImageConvertor() {
        log.info("scheduler for image creation has started!!");
        List<RequestEntity> readyRequests = requestDAO.getAllByStatus(RequestStatus.READY.getLiteral());
        log.info("found {} request ready for processing", readyRequests.size());
        for (RequestEntity request: readyRequests) {
            log.info("processing request with Id: {}", request.getId());
            byte[] imageBytes = new byte[0];
            try {
                imageBytes = generateImage(request.getImageCaption());
            } catch (Exception e) {
                log.error("Error occurred while calling image generator api", e);
                request.setStatus(RequestStatus.FAILURE.getLiteral());
                requestDAO.save(request);
                continue;
            }

            String generatedFileName = request.getId() + "-new";
            try {
                storeInObjectStorage(imageBytes, generatedFileName);
            } catch (Exception e) {
                log.error("Error occurred while saving generated image to object storage", e);
                request.setStatus(RequestStatus.FAILURE.getLiteral());
                requestDAO.save(request);
                continue;
            }

            request.setNewImageURL(bucketEndpoint + "/" + bucketName + "/" + generatedFileName);
            request.setStatus(RequestStatus.DONE.getLiteral());
            log.info("saving request with id: {} with status done to DB", request.getId());
            RequestEntity saved = requestDAO.save(request);
            log.info("finished saving request with id: {} with status done to DB", request.getId());
            sendEmail(saved);
        }
    }

    private byte[] generateImage(String caption) {
        log.info("calling image generator api!!!");
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        String input = "{\"inputs\":" + caption + "\"}";

        HttpEntity<String> requestEntity = new HttpEntity<>(input, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, byte[].class);
        log.info("finished calling image generator api!!!");
        return response.getBody();
    }

    private void storeInObjectStorage(byte[] imageBytes, String fileName) {
        log.info("saving the generated image with name: {}", fileName);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType("image/jpeg")
                .build();
        log.info("finished saving the generated image with name: {}", fileName);
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
    }

    private void sendEmail(RequestEntity savedEntity) {
        log.info("sending an email to {}", savedEntity.getEmail());
        Email email = new Email();

        email.setFrom("CloudApp", "cloudapp@trial-jy7zpl96xqpl5vx6.mlsender.net");
        email.addRecipient("user", savedEntity.getEmail());

        email.setSubject("Request Result");

        email.setPlain(savedEntity.getNewImageURL());

        MailerSend ms = new MailerSend();

        ms.setToken(mailToken);

        try {
            MailerSendResponse response = ms.emails().send(email);
            log.info("finished sending an email with id {} to {}",response.messageId, savedEntity.getEmail());
        } catch (MailerSendException e) {
            log.error("Error occurred while sending email", e);
            savedEntity.setStatus(RequestStatus.FAILURE.getLiteral());
            requestDAO.save(savedEntity);
        }
    }
}
