package com.leaderbet.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("LABEL")
public class LabelPair extends AbstractPair {
    public LabelPair(Integer dataId, Integer labelId) {
        super(dataId, labelId);
    }

    @OneToOne
    @JoinColumn(name = "LABEL_ID", referencedColumnName = "ID",insertable = false, updatable = false)
    Label label;

    @OneToOne
    @JoinColumn(name = "DATA_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    LabelGroup labelGroup;

}