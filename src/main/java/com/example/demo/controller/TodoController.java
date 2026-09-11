package com.example.demo.controller;

import com.example.demo.entity.Todo;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.TodoRepository;

// ===== 下面这几个注解都来自 Spring Web，作用是把 URL 请求映射到 Java 方法上 =====
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Todo 控制层（Controller）—— 对外提供 HTTP 接口。
 *
 * 一次请求的完整流转路径是这样的：
 *   浏览器发 GET /todo
 *     → Tomcat（Spring Boot 内置的 Web 服务器）接住请求
 *     → Spring 根据注解找到 list() 方法
 *     → list() 调用 TodoRepository.findAll()
 *     → Repository 让 Hibernate 生成并执行 SELECT 语句，查 MySQL
 *     → 查出的 List<Todo> 一路返回上来
 *     → 由 Jackson（JSON 序列化库）把 Java 对象自动转成 JSON
 *     → 浏览器收到 [{"id":1,"title":"买菜","done":false}] 这样的文本
 *
 * 整个过程你一行 SQL 和一行 JSON 拼装代码都没写，全是框架替你做的。
 */
@RestController     // 【核心注解】= @Controller + @ResponseBody 的组合。
                    // @Controller    ：告诉 Spring"这个类是个控制器，启动时扫到它就注册成 Bean，
                    //                   并且它负责处理 HTTP 请求"。
                    // @ResponseBody  ：方法的返回值直接写进 HTTP 响应体，按 JSON 输出，
                    //                   而不是当成"页面名"去找 HTML 模板。
                    //
                    // 对比记忆：做接口（返回 JSON）用 @RestController；
                    //          做网页（返回 HTML 页面）才用 @Controller。
                    // 如果你写了 @RestController 却期待它跳转页面，是不会跳的。
@RequestMapping("/todo")    // 类级别的路径前缀。
                            // 它和下面方法级注解的路径是"拼接"关系：
                            //   完整路径 = 类上的前缀 + 方法上的路径
                            // 当前类上写 /todo，方法上的 @GetMapping 没写路径（等于空），
                            // 所以最终接口就是 GET /todo —— 这正是你之前 404 时该访问的地址。
                            //
                            // ⚠️ 一个 Spring Boot 3+ 的行为变化：结尾斜杠默认不再兼容。
                            //    /todo 能访问，但 /todo/ 会返回 404。旧教程里的写法现在不适用了。
public class TodoController {

    // ===== 构造器注入（推荐做法）=====
    // final 表示这个引用一旦赋值就不能再改，保证依赖不会被意外替换。
    private final TodoRepository todoRepository;

    // Spring 看到这个控制器需要被创建时，会自动把容器里已有的 TodoRepository 实例传进来，
    // 这个机制叫"依赖注入"（DI / 控制反转 IoC）。
    // 你不需要自己 new TodoRepository()，也 new 不出来 —— 它是接口，实现类由 Spring 动态代理生成。
    //
    // 小知识：Spring 4.3 以后，如果一个类只有一个构造函数，@Autowired 可以省略，
    // Spring 会自动认定它就是要用的注入方式。所以这里看不到 @Autowired 是正常的，不是漏写。
    //
    // 为什么用构造器注入，而不是在字段上直接打 @Autowired？
    //   1. 能配合 final，依赖不可变，更安全
    //   2. 依赖缺失时启动阶段就报错，不会拖到运行时才空指针
    //   3. 写单元测试时可以直接 new TodoController(mockRepository) 塞个假对象进去，不依赖 Spring 容器
    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping     // 只处理 HTTP GET 请求，路径为空表示沿用类上的 /todo。
                    // 它的同族注解，以后加接口会用到：
                    //   @PostMapping   → POST   新增数据（如 POST /todo 创建一条待办）
                    //   @PutMapping    → PUT    整体更新
                    //   @PatchMapping  → PATCH  局部更新（如只改 done 字段）
                    //   @DeleteMapping → DELETE 删除
                    // 这些合称 REST 风格：用同一个路径 /todo，靠不同的 HTTP 动词区分要做的操作。
    public List<Todo> list(){
        // findAll() 是继承自 JpaRepository 的方法，查全表。
        // 返回的 List<Todo> 会被自动序列化成 JSON 数组；表里没数据时就是空数组 []，不是 null，也不是报错。
        return todoRepository.findAll();
    }
    @PostMapping
    public Todo create(@RequestBody Todo todo){
        todo.setId(0);
        return todoRepository.save(todo);
    }
    @GetMapping("/{id}")
    public Todo getById(@PathVariable Long id ){
        return todoRepository.findById(id).orElseThrow(() -> new NotFoundException("id 不存在：" + id));
    }
    @PutMapping("/{id}")
    public Todo update(@PathVariable long id, @RequestBody Todo todo){
        Todo existing  = todoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("id 不存在：" + id));
        if(todo.getTitle() != null){
        existing.setTitle(todo.getTitle());
        }
        if(todo.getDone() != null) {
            existing.setDone(todo.getDone());
        }
        return todoRepository.save(existing);

    }
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id){
        if(!todoRepository.existsById(id)){
            throw new NotFoundException("id 不存在：" + id);
    }
        todoRepository.deleteById(id);
        return "删除成功";
    }
}
