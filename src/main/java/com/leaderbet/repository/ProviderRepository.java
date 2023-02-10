package com.leaderbet.repository;

import com.leaderbet.Entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Integer>, JpaSpecificationExecutor<Provider> {
}
