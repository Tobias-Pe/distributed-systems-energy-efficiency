package edu.hm.peslalz.thesis.feedservice.entity;

import lombok.Data;

import java.util.List;

@Data
public class PostDTO {
    private Integer id;
    private Integer userId;
    private String text;
    private Integer likes;
    private List<CommentDTO> comments;
    private List<CategoryDTO> categories;

    @Data
    public static class CommentDTO {
        private Integer id;
        private Integer userId;
        private String text;
        private Integer likes;
    }

    @Data
    public static class CategoryDTO {
        private String name;
    }
}
