package com.leaderbet.repository;

import com.leaderbet.Entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface LabelRepository extends JpaRepository<Label, Integer> {
    @Query("select l from Label l join fetch l.slotPairs s join fetch s.game where l.id in (?1)")
    Set<Label> findByIdWithGames(Set<Integer> id);
}
