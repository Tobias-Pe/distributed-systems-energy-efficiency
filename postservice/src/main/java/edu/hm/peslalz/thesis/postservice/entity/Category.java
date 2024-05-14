package edu.hm.peslalz.thesis.postservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Category {
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany
    private Set<Post> posts = new HashSet<>();
}
