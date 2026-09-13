-- 给已有的 todo 表增加优先级字段。
-- 旧数据统一使用 MEDIUM，避免添加非空字段时失败。

ALTER TABLE todo
    ADD COLUMN priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM';