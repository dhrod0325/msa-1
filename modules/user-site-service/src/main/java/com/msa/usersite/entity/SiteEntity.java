package com.msa.usersite.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("site")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteEntity {
    @Id
    private Long id;

    private String siteCode;
    private String name;
    private String pathPrefix;
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
