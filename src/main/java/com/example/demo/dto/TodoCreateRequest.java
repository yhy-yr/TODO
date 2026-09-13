package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TodoCreateRequest {

    @NotBlank(message = "标题不能为空") // 标题不能为 null、空字符串或纯空格
    @Size(max = 50, message = "标题不能超过五十字") // 限制标题最多为 50 个字符
    private String title;

    private Boolean done;

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
}
