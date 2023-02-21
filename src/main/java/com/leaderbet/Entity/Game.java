package com.leaderbet.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

    @JsonManagedReference
    @OneToMany(mappedBy="game")
    private Set<SlotPair> slotPairs;

    @Transient
    private List<String> labelIds;

    @Transient
    private double sort;

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", providerId=" + providerId +
                ", name='" + name + '\'' +
                '}';
    }
}
