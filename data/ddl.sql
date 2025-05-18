SET NAMES utf8mb4;

DROP TABLE IF EXISTS `event_definitions`;
CREATE TABLE `event_definitions`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `service`     varchar(255) NOT NULL,
    `event_code`  varchar(100) NOT NULL,
    `description` varchar(255) DEFAULT NULL,
    `enabled`     tinyint(1) DEFAULT 1,
    `created_at`  timestamp NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`id`),
    UNIQUE KEY `event_code` (`event_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

BEGIN;
INSERT INTO `event_definitions` (`id`, `service`, `event_code`, `description`, `enabled`, `created_at`)
VALUES (1, 'gateway', 'filter', 'test', 1, '2025-05-18 05:52:48');
COMMIT;

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`
(
    `id`       bigint(20) NOT NULL AUTO_INCREMENT,
    `username` varchar(255) NOT NULL,
    `password` varchar(255) NOT NULL,
    `role`     varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

BEGIN;
INSERT INTO `users` (`id`, `username`, `password`, `role`)
VALUES (1, 'admin', '$2a$10$ExVtl13B8ZBW.ykAGW9hMOijg/be1Xr49LDG.3zYRwzZK.SwmS6m2', NULL);
COMMIT;

SET
FOREIGN_KEY_CHECKS = 1;
