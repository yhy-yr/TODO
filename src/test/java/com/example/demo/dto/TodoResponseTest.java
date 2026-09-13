package com.example.demo.dto;

import com.example.demo.entity.Todo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TodoResponseTest {

    @Test // 告诉 JUnit：这是一个需要执行的测试方法
    void from_shouldConvertTodoToResponse() {
        // 第一步：准备一条测试数据
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("学习 JUnit");
        todo.setDone(false);

        // 第二步：调用需要测试的方法
        TodoResponse response = TodoResponse.from(todo);

        // 第三步：检查转换后的结果是否符合预期
        assertEquals(1L, response.getId());
        assertEquals("学习 JUnit", response.getTitle());
        assertEquals(false, response.getDone());
    }
}