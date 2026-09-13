package com.example.demo.dto;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TodoUpdateRequest {

    @Pattern(regexp = ".*\\S.*", message = "标题不能为空") // 有标题时不能是空字符串或纯空格，允许不传
    @Size(max = 50, message = "标题不能超过五十字") // 有标题时最多为 50 个字符
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
