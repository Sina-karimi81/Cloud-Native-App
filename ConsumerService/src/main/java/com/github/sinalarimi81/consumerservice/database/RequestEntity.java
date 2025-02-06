package com.github.sinalarimi81.consumerservice.database;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Request")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "new_image_url")
    private String newImageURL;
    @Column(name = "image_caption")
    private String imageCaption;
}
