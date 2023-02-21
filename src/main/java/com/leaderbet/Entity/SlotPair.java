package com.leaderbet.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("SLOT")
public class SlotPair extends AbstractPair {
    public SlotPair(Integer dataId, Integer labelId) {
        super(dataId, labelId);
    }

    public SlotPair(Integer dataId, Integer labelId, double sort) {
        super(dataId, labelId, sort);
    }

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "DATA_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Game game;
}