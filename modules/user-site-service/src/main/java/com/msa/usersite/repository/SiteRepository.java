package com.msa.usersite.repository;

import com.msa.usersite.entity.SiteEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SiteRepository extends ReactiveCrudRepository<SiteEntity, Long> {
    Mono<SiteEntity> findBySiteCode(String siteCode);
}
