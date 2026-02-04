package com.heony.flowteam.enterprisedevrecruitmenttask.repository;

import com.heony.flowteam.enterprisedevrecruitmenttask.domain.BlockedExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlockedExtensionRepository extends JpaRepository<BlockedExtension, UUID> {
    
    boolean existsByExtension(String extension);
    
    Optional<BlockedExtension> findByExtension(String extension);
    
    void deleteByExtension(String extension);
    
    long count();
}
