package com.msa.usersite.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("site_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteUserEntity {
    @Id
    private Long id;
    private Long siteId;
    private String username;
    private String password;
    private String role;

    private LocalDateTime createdAt;
}
