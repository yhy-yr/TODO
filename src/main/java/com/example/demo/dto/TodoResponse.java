package com.example.demo.dto;

import com.example.demo.entity.Todo;

public class TodoResponse {

    private long id;
    private String title;
    private Boolean done;

    public TodoResponse(long id, String title, Boolean done) {
        this.id = id;
        this.title = title;
        this.done = done;
    }

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDone()
        );
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
}