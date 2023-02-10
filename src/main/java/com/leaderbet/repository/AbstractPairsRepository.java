package com.leaderbet.repository;

import com.leaderbet.Entity.AbstractPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AbstractPairsRepository extends JpaRepository<AbstractPair, Integer> {
    AbstractPair findByDataTypeAndDataIdAndLabelId(String dataType, Integer dataId, Integer labelId);

    List<AbstractPair> findByDataTypeAndDataId(String dataType, Integer dataId);

    Boolean existsByDataTypeAndDataIdAndLabelId(String dataType, Integer dataId, Integer labelId);

    List<AbstractPair> findByLabelId(int labelId);

    List<AbstractPair> findByDataId(int labelId);
}
