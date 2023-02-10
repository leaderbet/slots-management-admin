package com.leaderbet.Entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
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
}