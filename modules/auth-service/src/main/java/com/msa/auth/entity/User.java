package com.msa.auth.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {

    @Id
    private Long id;

    private String username;
    private String password;

    private String role = "";

    private Integer loginFailCount = 0;
    private LocalDateTime accountLockedUntil;
    private LocalDateTime lastPasswordChangeDate;
    private String currentSessionId;
    private LocalDateTime lastLoginDate;

    private String accountStatus = "ACTIVE";
}
