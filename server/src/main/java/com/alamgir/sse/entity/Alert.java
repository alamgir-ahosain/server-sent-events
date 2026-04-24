package com.alamgir.sse.entity;

import com.alamgir.sse.dto.enums.ALERT_TYPE;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ALERT_TYPE type;

    @Column(nullable = false, length = 500)
    private String description;

    // UNICAST -> target farmer
    // BROADCAST -> all farmer
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // true = broadcast, false = unicast
    @Column(nullable = false)
    private boolean broadcast;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

