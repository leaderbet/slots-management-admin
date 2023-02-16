package com.leaderbet.repository;

import com.leaderbet.Entity.AbstractPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AbstractPairsRepository extends JpaRepository<AbstractPair, Integer> {

}
