package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity // 将这个类声明为 JPA 实体
@Table(name = "todo") // 将实体映射到数据库中的 todo 表
@EntityListeners(AuditingEntityListener.class) // 监听实体的新增和修改操作，并自动填写审计字段
public class Todo {

    @Id // 声明主键字段
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 使用数据库自增方式生成主键
    private long id;

    @NotBlank(message = "标题不能为空") // 标题不能为 null、空字符串或纯空格
    @Size(max = 50, message = "标题不能超过五十字") // 限制标题最多为 50 个字符
    private String title;

    private Boolean done;
    @CreatedDate// 第一次保存 Todo 时自动填写创建时间
    @Column(updatable = false) // 创建后不允许通过 UPDATE 修改该字段
    private LocalDateTime createdAt;
    @LastModifiedDate // 新增或修改 Todo 时自动填写最后更新时间
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING) // 将枚举名称 LOW、MEDIUM、HIGH 作为字符串保存
    @Column(nullable = false) // priority 字段不允许为 null
    private Priority priority = Priority.MEDIUM;


    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getDone() {
        return done;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Todo() {
    }
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
