package com.msa.usersite.service;

import com.msa.usersite.dto.SiteDto;
import com.msa.usersite.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SiteService {
    private final SiteRepository siteRepository;

    public Mono<SiteDto> getSiteByCode(String siteCode) {
        return siteRepository.findBySiteCode(siteCode)
                .map(SiteDto::fromEntity)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "사이트 없음")));
    }
}
