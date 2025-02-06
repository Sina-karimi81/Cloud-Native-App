package com.github.sinakarimi81.cloudexercise01.business;

import com.github.sinakarimi81.cloudexercise01.common.RequestStatus;
import com.github.sinakarimi81.cloudexercise01.database.RequestDAO;
import com.github.sinakarimi81.cloudexercise01.database.RequestEntity;
import com.github.sinakarimi81.cloudexercise01.objectStorage.BucketService;
import com.github.sinakarimi81.cloudexercise01.queue.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Slf4j
public class RequestService {

    private final RequestDAO requestDAO;
    private final BucketService bucketService;
    private final Producer rabbitMqSender;

    public RequestService(RequestDAO requestDAO, BucketService bucketService, Producer rabbitMqSender) {
        this.requestDAO = requestDAO;
        this.bucketService = bucketService;
        this.rabbitMqSender = rabbitMqSender;
    }


    public Long uploadPicture(MultipartFile image, String emailAddress) {
        // save in database
        RequestEntity entity = new RequestEntity();
        entity.setEmail(emailAddress);
        entity.setStatus(RequestStatus.PENDING.getLiteral());
        RequestEntity save = requestDAO.save(entity);

        // Upload on object storage
        try {
            bucketService.uploadImage(image, String.valueOf(save.getId()));
        } catch (Exception e) {
            log.error("Error occurred while saving image with id {} to object storage", save.getId(), e);
            save.setStatus(RequestStatus.FAILURE.getLiteral());
            requestDAO.save(save);
        }

        // put on rabbit
        try {
            rabbitMqSender.pushMessageToQueue(save.getId());
        } catch (Exception e) {
            log.error("Error occurred while pushing message to rabbitmq. setting request {} status to failure", save.getId(), e);
            save.setStatus(RequestStatus.FAILURE.getLiteral());
            requestDAO.save(save);
        }

        return save.getId();
    }

    public Optional<RequestEntity> getRequestEntity(Long requestId) {
        return requestDAO.findById(requestId);
    }
}
