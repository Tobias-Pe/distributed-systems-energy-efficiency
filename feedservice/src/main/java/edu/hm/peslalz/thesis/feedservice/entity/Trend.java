package edu.hm.peslalz.thesis.feedservice.entity;


import java.io.Serial;
import java.io.Serializable;

public record Trend(String identifier, Integer trendPoints) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
