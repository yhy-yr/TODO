-- 创建 Todo 表。
-- 对于当前已经存在的数据库，这个脚本会被 baseline 跳过；
-- 将来连接一个全新的空数据库时，Flyway 会执行它。

CREATE TABLE todo
(
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    title      VARCHAR(50) NOT NULL,
    done       BIT(1)      NOT NULL DEFAULT b'0',
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (id)
);