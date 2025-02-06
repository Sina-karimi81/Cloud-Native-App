package com.github.sinakarimi81.cloudexercise01.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Producer {

    private final RabbitTemplate rabbitTemplate;

    public Producer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void pushMessageToQueue(Long imageId) {
        log.info("pushing image with id: {} to message queue", imageId);
        rabbitTemplate.convertAndSend("q.user-request", String.valueOf(imageId));
        log.info("finished pushing image with id: {} to message queue", imageId);
    }
}
