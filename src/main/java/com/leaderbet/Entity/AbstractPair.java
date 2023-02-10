package com.leaderbet.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "label_pairs")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "data_type")
public abstract class AbstractPair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false, insertable = false)
    Integer id;

    @Column(name = "DATA_ID")
    Integer dataId;

    @Column(name = "LABEL_ID")
    Integer labelId;
    Double sort = 0.00;

    @Column(updatable = false, insertable = false, name = "data_type")
    String dataType;

    public AbstractPair() {
    }

    public AbstractPair(Integer dataId, Integer labelId) {
        this.dataId = dataId;
        this.labelId = labelId;
    }

}
