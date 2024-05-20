package edu.hm.peslalz.thesis.postservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class ImageData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Integer id;

    @Lob
    @Column(name = "imagebytes")
    private byte[] imageBytes;

    @Column(name = "imagetype")
    private String imageType;

    @OneToOne(mappedBy = "imageData")
    private Post post;

    public ImageData(MultipartFile image) {
        try {
            this.imageBytes = image.getBytes();
            this.imageType = image.getContentType();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image could not be read", e);
        }
    }
}
