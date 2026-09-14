package com.example.demo.controller;

import com.example.demo.entity.Priority;
import com.example.demo.entity.Todo;
import com.example.demo.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(TodoController.class)

class TodoControllerTest {

    @Autowired // 从 Spring 容器中取得自动创建的 MockMvc
    private MockMvc mockMvc;
    @MockitoBean
    private TodoService todoService;

    @Test // 声明这是一个测试方法
    void create_shouldReturn400_whenPriorityIsInvalid() throws Exception {
        // 模拟发送 POST /todo 请求。
        // priority 使用了小写 high，无法转换成 Priority 枚举。
        mockMvc.perform(
                        post("/todo")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "测试错误枚举",
                                          "priority": "high"
                                        }
                                        """)
                )
                // 检查真实的 HTTP 状态码是否为 400
                .andExpect(status().isBadRequest())

                // 检查响应 JSON 中的 code 字段
                .andExpect(jsonPath("$.code").value(400))

                // 检查响应 JSON 中的 msg 字段
                .andExpect(jsonPath("$.msg").value(
                        "请求体格式错误，请检查 JSON 和字段取值"
                ));
    }
    @Test // 声明这是一个测试方法
    void getById_shouldReturnTodo_whenTodoExists() throws Exception {
        // 准备一条假的 Todo 数据
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("学习 Controller 测试");
        todo.setDone(false);
        todo.setPriority(Priority.HIGH);

        // 规定假 Service 的行为：
        // Controller 查询 id=1 时，让 Service 返回上面的 todo。
        when(todoService.getById(1L))
                .thenReturn(todo);

        // 模拟发送 GET /todo/1 请求
        mockMvc.perform(get("/todo/1"))
                // 检查 HTTP 状态码
                .andExpect(status().isOk())

                // 检查统一响应结构
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("成功"))

                // 检查 data 中的 Todo 数据
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value(
                        "学习 Controller 测试"
                ))
                .andExpect(jsonPath("$.data.done").value(false))
                .andExpect(jsonPath("$.data.priority").value("HIGH"));

        // 确认 Controller 确实调用了 Service 的 getById(1)
        verify(todoService).getById(1L);
    }
}