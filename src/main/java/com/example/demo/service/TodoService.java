package com.example.demo.service;

import com.example.demo.entity.Todo;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Todo 业务层（Service）—— 项目的"大脑"，所有业务规则都写在这里。
 *
 * ===== 为什么要有这一层 =====
 *
 * 改造前：Controller 直接调 Repository，业务逻辑和 HTTP 处理混在一起。
 * 改造后：Controller（管 HTTP） → Service（管业务） → Repository（管数据库）
 *
 * 每一层只干自己该干的事：
 *   Controller ：接参数、返 JSON、定状态码。不该懂业务规则。
 *   Service    ：业务规则、校验、多表协作。不该知道 HTTP 长什么样。
 *   Repository ：增删改查。不该懂业务规则。
 *
 * 判断"这段代码该放哪"的方法：问自己"如果以后要做一个手机 App 或定时任务来调这个功能，
 * 这段代码还需要吗？" 需要 → 放 Service；不需要（纯 HTTP 相关）→ 放 Controller。
 *
 * ===== 一个重要变化 =====
 * 注意这个类的方法返回的是 Todo / List<Todo> / void，【不是】Result<Todo>。
 * 因为 Result 是"HTTP 响应的格式"，属于 Controller 层的职责。
 * Service 只管业务结果，不该知道返回值最终会被包装成 JSON。
 * 这样以后如果加一个定时任务调用 Service，它拿到的是干净的对象，不用去拆 Result。
 */
@Service    // 告诉 Spring：这个类是个业务组件，启动时扫描到它就注册成 Bean。
            // 和 @RestController 一样，本质都是 @Component 的特化版本，区别只是语义：
            //   @Service      → 业务逻辑
            //   @Repository   → 数据访问
            //   @Controller   → HTTP 接口
            // 功能上三者几乎一样，但用语义化注解能让代码一眼看出每层的职责。
public class TodoService {

    // 构造器注入，和你 Controller 里的写法完全一样。
    // 只是现在 Service 注入 Repository，Controller 注入 Service。
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    /**
     * 查询全部待办。
     *
     * @Transactional(readOnly = true) 表示这个方法只读、不改数据。
     * readOnly = true 是个优化提示：告诉底层"别准备回滚了"，Hibernate 会跳过脏检查（dirty checking），
     * 数据量大时能省一些性能。纯查询方法建议都加上。
     */
    @Transactional(readOnly = true)
    public List<Todo> list() {
        return todoRepository.findAll();
    }

    /**
     * 新增一条待办。
     *
     * @Transactional 表示这个方法在一个事务里执行：要么全部成功，要么全部回滚。
     * 单个 save 看起来不需要事务，但这是好习惯——以后这个方法里如果要同时写两张表
     * （比如新增待办的同时记一条操作日志），事务能保证两边要么都成功要么都失败。
     *
     * ⚠️ @Transactional 必须加在 Service 的 public 方法上，加在 Controller 上不生效
     *    （Spring 的事务是靠代理实现的，Controller 不是代理的目标）。
     */
    @Transactional
    public Todo create(Todo todo) {
        todo.setId(0);
        // setId(0) 是为了强制走 INSERT。原因：实体里 id 是基本类型 long，默认值 0，
        // 而 Spring Data JPA 判断"新增还是更新"的规则是 id 为 null 或 0 → 视为新增。
        // 如果不归零，前端传了 {"id":5} 就会被当成更新去改 id=5 那条记录。
        return todoRepository.save(todo);
    }

    /**
     * 按 id 查询单条待办。
     *
     * findById 返回的是 Optional<Todo>（一个"盒子"，用来表达"可能查不到"）。
     * orElseThrow 的意思是：盒子里有东西就取出来，没有就抛异常。
     *
     * 抛出的 NotFoundException 会一路往上冒泡到 GlobalExceptionHandler，
     * 由它统一转成 {"code":404,"msg":"id 不存在：999","data":null}。
     * Service 自己不管"返回什么 HTTP 状态码"，那是 Controller 和异常处理器的事。
     */
    @Transactional(readOnly = true)
    public Todo getById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("id 不存在：" + id));
    }

    /**
     * 更新指定 id 的待办（部分更新：只改前端明确传了的字段）。
     *
     * 下面两个判空是之前修过的那个 bug：
     * 前端只传 {"title":"新标题"} 时，todo.getDone() 是 null，
     * 如果直接 existing.setDone(null)，就会把数据库里原有的值覆盖成 NULL。
     * 判空之后，"没传"就等于"不改"，只有明确传了值才覆盖。
     *
     * 这也是为什么这个方法上没有加 @Valid：
     * 实体上的 @NotBlank 会在 title 为 null 时报错，但"部分更新时不传 title"是合法的。
     * （规范做法是用校验分组 Validation Groups 区分 Create 和 Update，以后再学。）
     */
    @Transactional
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

    /**
     * 删除指定 id 的待办。
     *
     * 返回类型是 void —— 因为"删除成功"这句话是给前端看的提示语，属于 HTTP 层的职责，
     * Service 不该知道它。Controller 调用完这个方法后自己决定返回什么消息。
     *
     * 先 existsById 检查再删除，是为了能给出明确的 404 提示。
     * 如果直接 deleteById 一个不存在的 id，Spring Data 会抛一个不太友好的异常。
     *
     * ===== 留给你的练习 =====
     * 现在 Service 层有了，可以加真正的业务规则了。比如"已完成的待办不允许删除"：
     *   1. 把 existsById 换成 findById，拿到完整对象
     *   2. 判断 existing.getDone() 是不是 true
     *   3. 是的话抛一个新异常（建议新建 BusinessException，别复用 NotFoundException，
     *      因为"资源不存在"和"违反业务规则"是两件不同的事，HTTP 状态码也不同：
     *      前者 404，后者 400）
     *   4. 在 GlobalExceptionHandler 里给 BusinessException 加一个处理方法，返回 400
     * 这条规则一加，你就真正体会到 Service 层的意义了：
     * Controller 不知道这条规则，Repository 也不知道，只有 Service 知道。
     */
    @Transactional
    public void delete(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new NotFoundException("id 不存在：" + id);
        }
        todoRepository.deleteById(id);
    }
}
