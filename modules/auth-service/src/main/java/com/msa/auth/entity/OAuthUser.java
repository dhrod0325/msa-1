package com.msa.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("oauth_user")
public class OAuthUser {
    @Id
    private Long id;
    private String provider;           // kakao, naver, google
    private String providerUserId;     // 외부 서비스의 고유 사용자 ID
    private String email;
    private String nickname;
}