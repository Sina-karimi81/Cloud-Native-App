package com.github.sinakarimi81.cloudexercise01.services;

import com.github.sinakarimi81.cloudexercise01.business.RequestService;
import com.github.sinakarimi81.cloudexercise01.database.RequestEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;


@RestController
@Slf4j
public class RestServices {

    private final RequestService requestService;

    public RestServices(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping(value = "/submit")
    public ResponseEntity<Long> submitRequest(@RequestParam("image") MultipartFile file, @RequestParam("email") String email) {
        log.info("started to submit a request for email {}", email);
        Long imageId = requestService.uploadPicture(file, email);
        log.info("finished submitting a request with id: {}", imageId);
        return ResponseEntity.ok(imageId);
    }

    @GetMapping(value = "/status")
    public ResponseEntity<String> getRequestStatus(@RequestParam("id") Long requestId) {
        log.info("getting the status of request with id: {}", requestId);
        Optional<RequestEntity> requestEntity = requestService.getRequestEntity(requestId);
        return requestEntity.map(entity -> ResponseEntity.ok(entity.getStatus())).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
