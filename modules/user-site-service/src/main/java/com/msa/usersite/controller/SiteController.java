package com.msa.usersite.controller;

import com.msa.usersite.dto.SiteDto;
import com.msa.usersite.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/{siteId}")
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;

    @GetMapping("/info")
    public Mono<ResponseEntity<SiteDto>> getSiteInfo(@PathVariable String siteId) {
        return siteService.getSiteByCode(siteId).map(ResponseEntity::ok);
    }
}
