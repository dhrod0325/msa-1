SET NAMES utf8mb4;

DROP TABLE IF EXISTS `event_definitions`;
CREATE TABLE `event_definitions`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
    `service`     varchar(255) NOT NULL,
    `event_code`  varchar(100) NOT NULL,
    `description` varchar(255)      DEFAULT NULL,
    `enabled`     tinyint(1)        DEFAULT 1,
    `created_at`  timestamp    NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`id`),
    UNIQUE KEY `event_code` (`event_code`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

BEGIN;
INSERT INTO `event_definitions` (`id`, `service`, `event_code`, `description`, `enabled`, `created_at`)
VALUES (1, 'gateway', 'filter', 'test', 1, '2025-05-18 05:52:48');
COMMIT;

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`
(
    `id`                        BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `username`                  VARCHAR(255) NOT NULL,
    `password`                  VARCHAR(255) NOT NULL,
    `role`                      VARCHAR(50)  DEFAULT NULL,

    -- 로그인 실패 관련
    `login_fail_count`          INT          DEFAULT 0,
    `account_locked_until`      DATETIME     DEFAULT NULL,

    -- 비밀번호 변경 주기
    `last_password_change_date` DATETIME     DEFAULT NULL,

    -- 동시 로그인 제한을 위한 토큰/세션 UUID (선택사항)
    `current_session_id`        VARCHAR(100) DEFAULT NULL,
    -- 휴면 계정 처리
    `last_login_date`           DATETIME     DEFAULT NULL,
    `account_status`            VARCHAR(20)  DEFAULT 'ACTIVE', -- ACTIVE, DORMANT, LOCKED 등

    `created_at`                DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`                DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

BEGIN;
INSERT INTO `users` (`id`, `username`, `password`, `role`)
VALUES (1, 'admin', '$2a$10$ExVtl13B8ZBW.ykAGW9hMOijg/be1Xr49LDG.3zYRwzZK.SwmS6m2', NULL);

COMMIT;

CREATE TABLE site
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    site_code   VARCHAR(50)  NOT NULL UNIQUE COMMENT '사이트 식별자 (ex: site1, site2)',
    name        VARCHAR(100) NOT NULL COMMENT '사이트 이름',
    domain      VARCHAR(100) DEFAULT NULL COMMENT '서브도메인 (선택)',
    path_prefix VARCHAR(100) DEFAULT NULL COMMENT '서브패스 (ex: /site1)',
    is_active   BOOLEAN      DEFAULT TRUE COMMENT '사이트 활성화 여부',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE site_user
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    site_id    BIGINT       NOT NULL COMMENT 'site 테이블의 외래키',
    username   VARCHAR(50)  NOT NULL COMMENT '로그인 ID',
    password   VARCHAR(255) NOT NULL COMMENT 'BCrypt 암호화된 비밀번호',
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100),
    role       ENUM ('USER', 'MODERATOR') DEFAULT 'USER',
    is_enabled BOOLEAN                    DEFAULT TRUE,
    created_at DATETIME                   DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME                   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_site_user_site FOREIGN KEY (site_id)
        REFERENCES site (id)
        ON DELETE CASCADE,

    UNIQUE KEY uq_site_user (site_id, username)
);

BEGIN;

-- 사이트 등록
INSERT INTO site (site_code, name, path_prefix)
VALUES ('site1', '경북대학교', '/site1'),
       ('site2', '부산대학교', '/site2');

-- 사용자 등록
INSERT INTO site_user (site_id, username, password, name, email)
VALUES (1, 'hong123', '$2a$10$hash된값', '홍길동', 'hong@site1.com'),
       (2, 'lee456', '$2a$10$hash된값', '이순신', 'lee@site2.com');

commit;

CREATE TABLE oauth_user
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider         VARCHAR(50)  NOT NULL,      -- ex: kakao, naver, google
    provider_user_id VARCHAR(100) NOT NULL,-- ex: 카카오의 "id"
    email            VARCHAR(255),               -- 선택: 이메일 주소
    nickname         VARCHAR(100),               -- 선택: 표시 이름
    profile_image    VARCHAR(500),               -- 선택: 프로필 URL
    role             VARCHAR(20) DEFAULT 'USER', -- 권한
    created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_provider_user (provider, provider_user_id)
);
