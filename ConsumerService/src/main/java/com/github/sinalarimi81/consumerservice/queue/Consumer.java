package com.github.sinalarimi81.consumerservice.queue;

import com.github.sinalarimi81.consumerservice.common.RequestStatus;
import com.github.sinalarimi81.consumerservice.database.RequestDAO;
import com.github.sinalarimi81.consumerservice.database.RequestEntity;
import com.github.sinalarimi81.consumerservice.objectStorage.BucketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class Consumer {

    @Value("${hugging.face.text-generator.api}")
    private String apiUrl;
    @Value("${hugging.face.api.key}")
    private String apiKey;

    private final RequestDAO requestDAO;
    private final BucketService bucketService;

    public Consumer(RequestDAO requestDAO, BucketService bucketService) {
        this.requestDAO = requestDAO;
        this.bucketService = bucketService;
    }

    @RabbitListener(queues = {"q.user-request"})
    public void onUserRequest(String event) {
        log.info("An event was received from the queue. event: {}", event);
        Optional<RequestEntity> optionalRequestEntity = requestDAO.findById(Long.valueOf(event));
        if (optionalRequestEntity.isPresent()){
            RequestEntity entity = optionalRequestEntity.get();
            try {
                byte[] imageBytes = bucketService.downloadImage(event);
                String caption = getImageCaption(imageBytes);
                entity.setImageCaption(caption);
                entity.setStatus(RequestStatus.READY.getLiteral());
                log.info("saving request data with the new caption");
                requestDAO.save(entity);
            } catch (IOException e) {
                log.error("Error occurred while getting an event from rabbitmq", e);
                entity.setStatus(RequestStatus.FAILURE.getLiteral());
                requestDAO.save(entity);
            }
        } else {
            log.info("No request with Id: {} was found!!!", event);
        }
        log.info("finished processing received event");
    }

    private String getImageCaption(byte[] imageBytes) {
        log.info("calling caption generator api!!!");
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(imageBytes, headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);

        String res = "";
        String regex = "\"generated_text\"\\s*:\\s*\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response.getBody());
        if (matcher.find()){
            res = matcher.group(1);
        }
        log.info("finished calling caption generator api. result: {}", res);
        return res;
    }
}
