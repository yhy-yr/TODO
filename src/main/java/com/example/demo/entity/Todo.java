package com.example.demo.entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// jakarta.persistence.* 是 JPA 规范的注解包（@Entity、@Id、@Table 等都来自这里）。
// JPA（Java Persistence API）是 Java 官方的"对象 ←→ 数据库"映射标准，本身只是接口规范；
// 真正干活的是 Hibernate（你 pom 里的 spring-boot-starter-data-jpa 已经把它带进来了）。
// 简单说：你负责写 JPA 注解，Hibernate 负责把它翻译成 SQL 去执行。
import jakarta.persistence.*;

/**
 * Todo 实体类 —— 对应数据库里的 todo 表。
 *
 * 一个 Todo 对象 = 表里的一行记录；对象的每个字段 = 表里的一个列。
 * 这套机制叫 ORM（对象关系映射），好处是你不用手写 SQL，
 * 操作 Java 对象就等于在读写数据库。
 */
@Entity                 // 【必须】标记这个类是"实体"，Hibernate 才会管理它、才会在建表时考虑它。
                        // 漏了这个注解，这个类在 JPA 眼里就是个普通 Java 类，完全不认。
@Table(name = "todo")   // 指定映射到哪张表。
                        // 不写的话默认用类名 "Todo"，而 MySQL 在 Linux 下表名是区分大小写的，
                        // 很容易出现"本地能跑、上服务器报表不存在"的问题，所以显式写死更稳妥。
public class Todo {

    @Id     // 【必须】声明主键字段。一个实体必须有且仅有一个 @Id。
            // Hibernate 靠主键来判断"这是同一条记录"，findById、save 更新都依赖它。
    @GeneratedValue(strategy = GenerationType.IDENTITY)
            // 主键怎么生成。IDENTITY = 交给数据库自增，对应 MySQL 的 AUTO_INCREMENT。
            // 所以你 insert 时不用自己 setId()，MySQL 会自动分配，Hibernate 再把生成的 id 回填到对象里。
            //
            // 其他几种策略，了解一下就行：
            //   SEQUENCE —— 用数据库序列生成，Oracle / PostgreSQL 常用，MySQL 不支持
            //   TABLE    —— 用一张额外的表模拟序列，性能差，基本没人用
            //   AUTO     —— 让 Hibernate 自己挑，行为不可控（不同版本、不同数据库结果不一样），不推荐
    private long id;

    // 下面两个字段没有任何注解，走 Hibernate 的默认规则：
    //   列名 = 字段名，类型按 Java 类型推断（String → varchar(255)，Boolean → bit(1)）
    // 你数据库里 todo 表现在的结构就是这么来的（id bigint / title varchar(255) / done bit(1)）。
    @NotBlank(message = "标题不能为空")
    @Size(max = 50, message = "标题不能超过五十字")
    private String title;   // 任务标题

    // 这里刻意用包装类型 Boolean，而不是基本类型 boolean。
    // 原因：boolean 只能是 true/false，无法表示"数据库里这一列是 NULL"；
    // 用 Boolean 就能多出一个 null 状态（表示"还没设置过"），和数据库的可空列对得上。
    private Boolean done;   // 是否完成

    // ===== getter / setter =====
    // 这些不是可有可无的样板代码，Hibernate 是通过反射调用 getter/setter 来读写对象字段的。
    // 少写了某个 setter，从数据库查出来的数据就赋不进对象，字段会一直是 null —— 这是个很常见的坑。
    // （如果嫌手写麻烦，可以引入 Lombok 的 @Data 注解自动生成，但初学阶段手写一遍更容易理解原理。）

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getDone() {
        return done;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    // 【必须】无参构造函数。
    // Hibernate 从数据库查出数据后，是先调用无参构造 new 一个空对象，再逐个 set 字段的，
    // 所以这个构造函数不能删。
    // 注意：你只写了无参构造，Java 就不会再自动生成别的全参构造；
    // 如果想要 new Todo("标题", false) 这种写法，需要自己再加一个构造函数。
    public Todo(){
    }
}
