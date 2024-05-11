package edu.hm.peslalz.thesis.userservice.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
// prevent infinite loop on following each other
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
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

    @ManyToMany
    private Set<UserAccount> following = new HashSet<>();

    private Date created = new Date();
    private Date updated = new Date();

    @PreUpdate
    public void setLastUpdate() {  this.updated = new Date(); }
}
