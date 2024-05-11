package edu.hm.peslalz.thesis.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class UserAccount {
    public UserAccount(UserAccountRequest userAccountRequest) {
        this.username = userAccountRequest.getUsername();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;


}
