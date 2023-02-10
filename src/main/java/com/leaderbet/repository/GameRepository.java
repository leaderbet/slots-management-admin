package com.leaderbet.repository;

import com.leaderbet.Entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer>, JpaSpecificationExecutor<Game> {
}
