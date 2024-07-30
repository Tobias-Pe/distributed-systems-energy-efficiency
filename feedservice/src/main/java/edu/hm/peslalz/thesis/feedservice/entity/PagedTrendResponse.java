package edu.hm.peslalz.thesis.feedservice.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
@Data
public class PagedTrendResponse implements Serializable {
    public PagedTrendResponse(List<Trend> content) {
        this.content = content;
    }

    @Serial
    private static final long serialVersionUID = 2L;

    private List<Trend> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
