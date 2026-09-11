package com.example.demo.repository;


import com.example.demo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Todo 数据访问层（Repository）—— 负责和数据库打交道。
 *
 * 关键点：这是一个"接口"（interface），不是你写的实现类，但 Spring 会自动帮你生成实现。
 * 原理是 Spring Data JPA 在启动时用动态代理扫描到它，自动生成一个实现了所有方法的类，
 * 并注册成 Bean 交给容器管理。所以你能直接注入使用，却从头到尾没写过一行 SQL 或实现代码。
 *
 * 尖括号里的两个参数含义：
 *   Todo —— 这个 Repository 管理哪个实体类
 *   Long —— 该实体主键（id）的类型
 * 类型必须和实体里 @Id 字段的类型对得上，否则编译不过。
 *   注意这里用包装类 Long，而实体里字段写的是基本类型 long，JPA 允许这样混用。
 */
public interface TodoRepository extends JpaRepository<Todo, Long> {

    // 接口体是空的，但你已经白捡了一大堆现成方法，全都是继承 JpaRepository 得来的，常用的有：
    //
    //   findAll()              查全部记录            → SELECT * FROM todo
    //   findById(Long id)      按主键查一条          → SELECT ... WHERE id = ?
    //   save(Todo entity)      新增或更新（二合一）
    //                          id 为 null → INSERT；id 有值 → UPDATE
    //   deleteById(Long id)    按主键删除            → DELETE FROM todo WHERE id = ?
    //   count()                统计总条数            → SELECT COUNT(*) FROM todo
    //   existsById(Long id)    判断某条记录是否存在
    //   delete(Todo entity)    删除指定对象
    //
    // 另外 findById 返回的是 Optional<Todo>，不是 Todo 本身。
    // Optional 是个"盒子"，用来明确表达"可能查不到"这件事，
    // 强迫你用 orElse / isPresent / ifPresent 等方式先处理空值，避免直接拿 null 引发空指针异常。
    // 用的时候常见写法：todoRepository.findById(id).orElse(null)

    // ===== 如果以后想按字段查询，不用写 SQL，按命名规则起方法名就行 =====
    // Spring Data JPA 会解析方法名自动生成对应的 SQL，这叫"方法名派生查询"。例如：
    //
    //   List<Todo> findByDone(Boolean done);
    //                                    → SELECT * FROM todo WHERE done = ?
    //   List<Todo> findByTitleContaining(String keyword);
    //                                    → SELECT * FROM todo WHERE title LIKE '%keyword%'
    //   List<Todo> findByDoneFalseOrderByTitleAsc();
    //                                    → 查未完成项，并按标题升序排列
    //
    // 需要更复杂的 SQL 时，再加 @Query 注解手写即可。
}
