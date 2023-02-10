package com.leaderbet.repository;

import com.leaderbet.Entity.LabelPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelPairsRepository extends JpaRepository<LabelPair, Integer> {
    LabelPair findByDataIdAndLabelId(int dataId, int labelId);

    List<LabelPair> findByDataId(int dataId);

}
