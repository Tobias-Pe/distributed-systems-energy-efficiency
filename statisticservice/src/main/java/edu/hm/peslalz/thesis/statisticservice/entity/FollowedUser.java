package edu.hm.peslalz.thesis.statisticservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class FollowedUser {
    public FollowedUser(Integer userId) {
        this.userId = userId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Integer id;

    @Column(name = "userId", nullable = false, unique = true)
    private int userId;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private List<ActionProtocol> interactions = new ArrayList<>();
}
