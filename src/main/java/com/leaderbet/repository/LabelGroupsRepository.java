package com.leaderbet.repository;

import com.leaderbet.Entity.LabelGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelGroupsRepository extends JpaRepository<LabelGroup, Integer> {
}
