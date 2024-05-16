package edu.hm.peslalz.thesis.postservice.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "name")
public class Category {
    public Category(String name) {
        this.name = name;
    }

    @Id
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "categories")
    private Set<Post> posts;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;
        return getName().equals(category.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
