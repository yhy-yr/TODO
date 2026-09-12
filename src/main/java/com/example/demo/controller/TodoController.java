package com.example.demo.controller;

import com.example.demo.entity.Todo;
import com.example.demo.result.Result;
import com.example.demo.service.TodoService;

// ===== 下面这几个注解都来自 Spring Web，作用是把 URL 请求映射到 Java 方法上 =====
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Todo 控制层（Controller）—— 对外提供 HTTP 接口。
 *
 * ===== 本次重构：三层结构 =====
 *
 * 改造前：Controller 直接调 Repository，业务逻辑和 HTTP 处理混在一起。
 * 改造后：Controller（管 HTTP） → Service（管业务） → Repository（管数据库）
 *
 * 一次请求的完整流转路径：
 *   浏览器发 GET /todo
 *     → Tomcat（Spring Boot 内置的 Web 服务器）接住请求
 *     → Spring 根据注解找到 list() 方法
 *     → list() 调用 todoService.list()        ← 注意：现在多绕了一层 Service
 *     → Service 调用 todoRepository.findAll()
 *     → Repository 让 Hibernate 生成并执行 SELECT 语句，查 MySQL
 *     → List<Todo> 一路返回上来
 *     → Controller 用 Result.success() 包一层，统一成 {code,msg,data}
 *     → 由 Jackson（JSON 序列化库）把 Java 对象自动转成 JSON
 *     → 浏览器收到 {"code":200,"msg":"成功","data":[...]}
 *
 * ===== 重构后这个类的职责变得非常窄 =====
 * 只做三件事：① 接收 HTTP 参数 ② 调 Service ③ 把结果包成 Result 返回。
 * 所以你会看到下面每个方法都只有两三行 —— 这是对的，Controller 本来就该"薄"。
 * 一旦你发现 Controller 里出现了 if 判断业务规则、或者注入了两个以上的 Repository，
 * 那就说明有代码放错层了，应该挪到 Service 去。
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

    // ===== 本次重构的关键改动 =====
    // 注入的对象从 TodoRepository 换成了 TodoService。
    //
    // 为什么要换？因为 Controller 不该直接碰数据库。
    // 业务规则（比如"已完成的待办不能删"）应该写在 Service 里，
    // 这样以后如果加一个定时任务或手机 App 的接口来调同样的功能，
    // 规则会自动生效，不用在每个入口重复写一遍。
    //
    // 注意：NotFoundException、TodoRepository 这两个 import 已经从这个文件里删掉了 ——
    // 因为异常现在由 Service 抛、由 GlobalExceptionHandler 接，Controller 完全不用知道它们存在。
    private final TodoService todoService;

    // Spring 看到这个控制器需要被创建时，会自动把容器里已有的 TodoService 实例传进来，
    // 这个机制叫"依赖注入"（DI / 控制反转 IoC）。
    // 你不需要自己 new TodoService()，Spring 会帮你构造并把 Repository 也注入进去。
    //
    // 小知识：Spring 4.3 以后，如果一个类只有一个构造函数，@Autowired 可以省略，
    // Spring 会自动认定它就是要用的注入方式。所以这里看不到 @Autowired 是正常的，不是漏写。
    //
    // 为什么用构造器注入，而不是在字段上直接打 @Autowired？
    //   1. 能配合 final，依赖不可变，更安全
    //   2. 依赖缺失时启动阶段就报错，不会拖到运行时才空指针
    //   3. 写单元测试时可以直接 new TodoController(mockService) 塞个假对象进去，不依赖 Spring 容器
    //      （有了 Service 层之后，第 3 点的价值更大了：测 Controller 时不用再伪造整个数据库）
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping     // 只处理 HTTP GET 请求，路径为空表示沿用类上的 /todo。
                    // 它的同族注解，下面都用到了：
                    //   @PostMapping   → POST   新增数据
                    //   @PutMapping    → PUT    更新
                    //   @DeleteMapping → DELETE 删除
                    //   @PatchMapping  → PATCH  局部更新（暂未使用）
                    // 这些合称 REST 风格：用同一个路径 /todo，靠不同的 HTTP 动词区分要做的操作。
    public Result<List<Todo>> list(){
        // 返回类型 Result<List<Todo>>：尖括号是"套娃"的，Result<...> 里面装一个 List<Todo>。
        // 所以 JSON 长这样：{"code":200,"msg":"成功","data":[{...},{...}]}
        // 表里没数据时 data 是空数组 []，不是 null，也不是报错。
        return Result.success(todoService.list());
    }

    @PostMapping
    // @Valid：让 Spring 在进入方法体之前，先按 Todo 实体上的 @NotBlank / @Size 校验参数。
    //         不通过就直接抛 MethodArgumentNotValidException，由 GlobalExceptionHandler 转成 400，
    //         你的业务代码根本不会执行 —— 脏数据进不了库。
    //
    // @RequestBody：把请求体里的 JSON 转成 Todo 对象。
    //               ⚠️ 只能用在 POST/PUT/PATCH 上。GET 没有请求体，加了会直接 400
    //               （上次 getById 就踩过这个坑）。
    //
    // 注意 @Valid 只加在 create 上，update 不加。原因见 TodoService.update() 的注释：
    // 部分更新时不传 title 是合法的，而 @NotBlank 会把它拦下来。
    public Result<Todo> create(@Valid @RequestBody Todo todo){
        // setId(0) 已经挪到 Service 里了 —— 因为"怎么保证走 INSERT"是业务规则，不是 HTTP 的事。
        return Result.success(todoService.create(todo));
    }

    @GetMapping("/{id}")
    // @PathVariable：把 URL 路径里 {id} 位置上的实际值取出来，塞进这个方法参数。
    //                 访问 /todo/2 时，id 就是 2。
    // 参数类型用 Long（包装类）而不是 long，因为 findById(Long) 要的是 Long，类型要对得上。
    public Result<Todo> getById(@PathVariable Long id){
        // 查不到时 Service 会抛 NotFoundException，一路冒泡到 GlobalExceptionHandler，
        // 由它统一转成 {"code":404,"msg":"id 不存在：999","data":null}。
        // Controller 这里一行 try-catch 都不用写 —— 这就是全局异常处理的好处。
        return Result.success(todoService.getById(id));
    }

    @PutMapping("/{id}")
    // 故意不加 @Valid：部分更新时前端可能只传 {"done":true}，
    // title 为 null 是合法的（表示"这个字段我不改"），加了校验反而会被 @NotBlank 拦住。
    public Result<Todo> update(@PathVariable Long id, @RequestBody Todo todo){
        // "没传就不改"的判空逻辑在 Service 里，那是业务规则。
        // 这就是之前修过的那个 bug：直接 set 会把数据库原值覆盖成 NULL。
        return Result.success(todoService.update(id, todo));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id){
        // Service 的 delete 返回 void —— 因为"删除成功"这句提示语是给前端看的，属于 HTTP 层职责，
        // Service 不该知道它。所以由 Controller 自己决定返回什么消息。
        todoService.delete(id);

        // 删除成功没有数据要返回，所以泛型是 Void（表示 data 里没东西）。
        //
        // 这里没用 Result.success()，因为它会把 msg 写死成"成功"，
        // 而你想保留"删除成功"这个更具体的提示，所以直接用构造函数传进去。
        // 如果嫌啰嗦，可以给 Result 加一个 success(String msg, T data) 的重载方法。
        return new Result<>(200, "删除成功", null);
    }
}
