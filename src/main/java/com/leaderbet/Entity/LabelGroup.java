package com.leaderbet.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "label_groups")
@NoArgsConstructor
@AllArgsConstructor
public class LabelGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false, insertable = false)
    private Integer id;
    private String name;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime deletedAt;
}
