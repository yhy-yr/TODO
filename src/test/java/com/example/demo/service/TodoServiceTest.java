package com.example.demo.service;

import com.example.demo.entity.Todo;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class) // 启用 Mockito，让 @Mock 和 @InjectMocks 生效
class TodoServiceTest {
    @Mock //创建一个假的 Repository，不连接真实数据库
    private TodoRepository todoRepository;
    @InjectMocks // 创建 TodoService，并注入上面的假 Repository
    private TodoService todoService;

    @Test // 声明这是一个可以运行的测试方法
    void delete_shouldThrowBusinessException_whenTodoIsDone() {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("已经完成的任务");
        todo.setDone(true);

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> todoService.delete(1L)
        );

        assertEquals("已完成的待办不能删除", exception.getMessage());

        verify(todoRepository, never()).delete(todo);
    }

    @Test // 声明这是一个测试方法
    void delete_shouldDeleteTodo_whenTodoIsNotDone() {
        // 准备一个未完成的任务
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("未完成的任务");
        todo.setDone(false);

        // 模拟 Repository：查询 id=1 时返回这个任务
        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo));

        // 调用删除方法
        todoService.delete(1L);

        // 验证 Repository 的 delete 方法确实执行了一次
        verify(todoRepository).delete(todo);
    }


}
