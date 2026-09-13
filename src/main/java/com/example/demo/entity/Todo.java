package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity // 将这个类声明为 JPA 实体
@Table(name = "todo") // 将实体映射到数据库中的 todo 表
public class Todo {

    @Id // 声明主键字段
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 使用数据库自增方式生成主键
    private long id;

    @NotBlank(message = "标题不能为空") // 标题不能为 null、空字符串或纯空格
    @Size(max = 50, message = "标题不能超过五十字") // 限制标题最多为 50 个字符
    private String title;

    private Boolean done;

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
}
