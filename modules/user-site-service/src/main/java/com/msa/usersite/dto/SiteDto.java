package com.msa.usersite.dto;

import com.msa.usersite.entity.SiteEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiteDto {
    private String siteCode;
    private String name;

    public static SiteDto fromEntity(SiteEntity entity) {
        return new SiteDto(entity.getSiteCode(), entity.getName());
    }
}

