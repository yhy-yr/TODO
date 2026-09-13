package com.example.demo.service;

import com.example.demo.entity.Todo;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.TodoRepository;
import org.springframework.boot.data.autoconfigure.web.DataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Todo> list(Boolean done, String keyword, Pageable pageable) {
        // keyword 不为 null，并且不是空字符串或纯空格时，才算有效关键字
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        // done 和 keyword 都没有传：查询全部
        if (done == null && !hasKeyword) {
            return todoRepository.findAll(pageable);
        }

        // 只传了 done：按照完成状态查询

        if (done != null && !hasKeyword) {
            return todoRepository.findByDone(done, pageable);
        }
        // trim() 去掉关键字前后的空格
        String cleanKeyword = keyword.trim();
        if (done == null) {
            return todoRepository.findByTitleContaining(
                    cleanKeyword,
                    pageable
            );
        }

        return todoRepository.findByDoneAndTitleContaining(
                done,
                cleanKeyword,
                pageable
        );
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
