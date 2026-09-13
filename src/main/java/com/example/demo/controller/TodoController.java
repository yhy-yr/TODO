package com.example.demo.controller;

import com.example.demo.dto.TodoCreateRequest;
import com.example.demo.dto.TodoResponse;
import com.example.demo.dto.TodoUpdateRequest;
import com.example.demo.entity.Todo;
import com.example.demo.result.Result;
import com.example.demo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // 将返回值转换为 JSON，并把这个类注册为接口控制器
@RequestMapping("/todo") // 为当前控制器中的所有接口添加 /todo 前缀
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping // 处理 GET /todo 请求
    public Result<List<TodoResponse>> list() {
        List<TodoResponse> data = todoService.list()
                .stream()
                .map(TodoResponse::from)
                .toList();
        return Result.success(data);
    }

    @PostMapping // 处理 POST /todo 请求
    public Result<TodoResponse> create(
            @Valid // 执行 TodoCreateRequest 中声明的参数校验
            @RequestBody // 将请求体中的 JSON 转换为 TodoCreateRequest
            TodoCreateRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDone(request.getDone() == null ? false : request.getDone());

        Todo saved = todoService.create(todo);
        return Result.success(TodoResponse.from(saved));
    }

    @GetMapping("/{id}") // 处理 GET /todo/{id} 请求
    public Result<TodoResponse> getById(
            @PathVariable // 读取 URL 路径中的 id
            Long id) {
        Todo todo = todoService.getById(id);
        return Result.success(TodoResponse.from(todo));
    }

    @PutMapping("/{id}") // 处理 PUT /todo/{id} 请求
    public Result<TodoResponse> update(
            @PathVariable // 读取 URL 路径中的 id
            Long id,
            @Valid // 执行 TodoUpdateRequest 中声明的参数校验
            @RequestBody // 将请求体中的 JSON 转换为 TodoUpdateRequest
            TodoUpdateRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDone(request.getDone());

        Todo updated = todoService.update(id, todo);
        return Result.success(TodoResponse.from(updated));
    }

    @DeleteMapping("/{id}") // 处理 DELETE /todo/{id} 请求
    public Result<Void> delete(
            @PathVariable // 读取 URL 路径中的 id
            Long id) {
        todoService.delete(id);
        return new Result<>(200, "删除成功", null);
    }
}
