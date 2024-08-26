package edu.hm.peslalz.thesis.feedservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 3L;

    private Integer id;
    private Integer userId;
    private String text;
    private Integer likes;
    private List<CategoryDTO> categories;

    @Data
    public static class CategoryDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String name;
    }
}
