package com.example.demo.controller;


import com.example.demo.entity.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import com.example.demo.dto.TodoCreateRequest;
import com.example.demo.dto.TodoResponse;
import com.example.demo.dto.TodoUpdateRequest;
import com.example.demo.entity.Todo;
import com.example.demo.result.Result;
import com.example.demo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 将返回值转换为 JSON，并把这个类注册为接口控制器
@RequestMapping("/todo") // 为当前控制器中的所有接口添加 /todo 前缀
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping // 处理 GET /todo 请求
    public Result<Page<TodoResponse>> list( @RequestParam(required = false) // 读取 ?done=...；required=false 表示可以不传
                                                Boolean done  ,@RequestParam(required = false) // 读取标题关键字，可以不传
            String keyword, @PageableDefault(
            size = 5,
            sort = "id",
            direction = Sort.Direction.DESC
    ) // 设置默认每页 5 条，并按照 ID 倒序排列
    Pageable pageable ) {
        Page<TodoResponse> data = todoService.list(done,keyword,pageable)

                .map(TodoResponse::from);

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
        todo.setPriority(
                request.getPriority() == null
                        ? Priority.MEDIUM
                        : request.getPriority()
        );

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

    @PatchMapping("/{id}") // 处理 PATCH /todo/{id} 请求
    public Result<TodoResponse> update(
            @PathVariable // 读取 URL 路径中的 id
            Long id,
            @Valid // 执行 TodoUpdateRequest 中声明的参数校验
            @RequestBody // 将请求体中的 JSON 转换为 TodoUpdateRequest
            TodoUpdateRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDone(request.getDone());
        todo.setPriority(request.getPriority());

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
