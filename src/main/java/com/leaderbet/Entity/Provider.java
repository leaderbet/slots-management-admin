package com.leaderbet.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "providers")
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false, insertable = false)
    private Integer id;

    private String platformName;
    private String providerName;
    private Integer operatorId;
    private Integer enabled;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime deletedAt;

    public Provider(Integer id, String platformName, String providerName, Integer operatorId, Integer enabled) {
        this.id = id;
        this.platformName = platformName;
        this.providerName = providerName;
        this.operatorId = operatorId;
        this.enabled = enabled;
    }
}
