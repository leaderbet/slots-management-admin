package com.leaderbet.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "game_list")
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false, insertable = false)
    private Integer id;
    //    @Column(name = "provider_id", insertable = false, updatable = false)
    private Integer providerId;
    private String subProviderId;
    private String operatorId;
    private String gameLaunchId;
    private String mobileGameLaunchId;
    private String gameRealId;
    private String mobileGameRealId;
    private String name;
    private Integer isMobile;
    private String imageConfig;
    private Integer enabled = 1;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime deletedAt;

    @Transient
    List<String> labelIds;


    public Game(Integer id, Integer providerId, String subProviderId, String operatorId, String gameLaunchId,
                String mobileGameLaunchId, String gameRealId, String mobileGameRealId,
                String name, Integer isMobile, String imageConfig, Integer enabled, List<String> labelIds) {
        this.id = id;
        this.providerId = providerId;
        this.subProviderId = subProviderId;
        this.operatorId = operatorId;
        this.gameLaunchId = gameLaunchId;
        this.mobileGameLaunchId = mobileGameLaunchId;
        this.gameRealId = gameRealId;
        this.mobileGameRealId = mobileGameRealId;
        this.name = name;
        this.isMobile = isMobile;
        this.imageConfig = imageConfig;
        this.enabled = enabled;
        this.labelIds = labelIds;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", providerId=" + providerId +
                ", name='" + name + '\'' +
                '}';
    }
}
