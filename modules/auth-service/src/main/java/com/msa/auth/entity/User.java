package com.msa.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "";

    @Column(nullable = false)
    private Integer loginFailCount = 0;

    private LocalDateTime accountLockedUntil;
    private LocalDateTime lastPasswordChangeDate;

    private String currentSessionId;
    private LocalDateTime lastLoginDate;

    @Column(nullable = false)
    private String accountStatus = "ACTIVE";
}
