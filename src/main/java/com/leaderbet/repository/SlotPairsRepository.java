package com.leaderbet.repository;

import com.leaderbet.Entity.SlotPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlotPairsRepository extends JpaRepository<SlotPair, Integer> {
    List<SlotPair> findByDataId(int dataId);

    SlotPair findByDataIdAndLabelId(int dataId, int labelId);
}
