package com.example.demo.service;

import com.example.demo.entity.Todo;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // 将这个类注册为业务层组件，由 Spring 管理
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Transactional(readOnly = true) // 开启只读事务，适用于纯查询操作
    public List<Todo> list() {
        return todoRepository.findAll();
    }

    @Transactional // 开启事务，发生运行时异常时回滚数据库操作
    public Todo create(Todo todo) {
        todo.setId(0);
        return todoRepository.save(todo);
    }

    @Transactional(readOnly = true) // 开启只读事务，适用于纯查询操作
    public Todo getById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("id 不存在：" + id));
    }

    @Transactional // 开启事务，保证更新操作完整执行或回滚
    public Todo update(Long id, Todo todo) {
        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("id 不存在：" + id));

        if (todo.getTitle() != null) {
            existing.setTitle(todo.getTitle());
        }
        if (todo.getDone() != null) {
            existing.setDone(todo.getDone());
        }
        return todoRepository.save(existing);
    }

    @Transactional // 开启事务，保证删除操作完整执行或回滚
    public void delete(Long id) {
        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("id 不存在：" + id));

        if (Boolean.TRUE.equals(existing.getDone())) {
            throw new BusinessException("已完成的待办不能删除");
        }

        todoRepository.delete(existing);
    }
}
