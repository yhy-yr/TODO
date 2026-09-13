package com.example.demo.dto;

import com.example.demo.entity.Priority;
import com.example.demo.entity.Todo;

import java.time.LocalDateTime;

public class TodoResponse {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private long id;
    private String title;
    private Boolean done;
    private Priority priority;

    public TodoResponse(long id, String title, Boolean done, LocalDateTime createdAt,
                        LocalDateTime updatedAt,Priority priority) {
        this.id = id;
        this.title = title;
        this.done = done;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.priority = priority;

    }

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDone(),
                todo.getCreatedAt(),
                todo.getUpdatedAt(),
                todo.getPriority()
        );
    }
    public Priority getPriority() {
        return priority;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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