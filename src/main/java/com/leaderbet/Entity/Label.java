package com.leaderbet.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "labels")
@NoArgsConstructor
@AllArgsConstructor
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false, insertable = false)
    private Integer id;
    private String name;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime deletedAt;


    public Label(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @OneToMany
    @JoinColumn(name = "LABEL_ID", referencedColumnName = "ID")
    Set<SlotPair> slotPairs = new HashSet<>();

}
