package com.msa.usersite.repository;

import com.msa.usersite.entity.SiteUserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SiteUserRepository extends ReactiveCrudRepository<SiteUserEntity, Long> {
    Mono<SiteUserEntity> findBySiteIdAndUsername(Long siteId, String username);
}
