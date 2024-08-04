package edu.hm.peslalz.thesis.statisticservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class ActionProtocol {
    public ActionProtocol(String action) {
        this.action = action;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "action", nullable = false)
    private String action;

    private Date registeredTimestamp;

    @PrePersist
    public void setRegisteredTimestamp() {  this.registeredTimestamp = new Date(); }

}
