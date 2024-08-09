package edu.hm.peslalz.thesis.postservice.entity;

import java.io.Serial;
import java.io.Serializable;

public record PostsRequestDTO(String category, Integer userId, int page, int size)  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
