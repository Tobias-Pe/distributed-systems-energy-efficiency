package edu.hm.peslalz.thesis.feedservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private Integer userId;

    public UserPreference(Integer userId) {
        this.userId = userId;
    }

    @ElementCollection(fetch = FetchType.LAZY)
    @MapKeyColumn(name = "category")
    @Column(name = "interaction_count")
    private Map<String, Integer> categoryInteractions = new HashMap<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @MapKeyColumn(name = "liked_user_id")
    @Column(name = "interaction_count")
    private Map<Integer, Integer> userInteractions = new HashMap<>();

    public void addCategoryInteraction(String category) {
        this.categoryInteractions.put(category, this.categoryInteractions.getOrDefault(category, 0) + 1);
    }

    public void addUserInteraction(Integer userId) {
        this.userInteractions.put(userId, this.userInteractions.getOrDefault(userId, 0) + 1);
    }

    @Version
    @JsonIgnore
    Integer version;
}
