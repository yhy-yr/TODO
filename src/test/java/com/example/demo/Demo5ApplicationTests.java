package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 项目自带的默认测试类，Spring Initializr 生成项目时会自动带上。
 *
 * 它只有一个作用：验证 Spring 容器能不能正常启动。
 * 如果配置写错、Bean 冲突、数据库连不上，这个测试就会失败 ——
 * 相当于一次"应用能不能跑起来"的体检。
 */
@SpringBootTest     // 告诉 JUnit：跑这个测试前，先把整个 Spring 应用上下文启动起来。
                    // 也就是说它会加载你所有的 Bean、连上 application.yml 里配的数据库，
                    // 和真正运行 main 方法的效果基本一致（只是不启动 Web 服务器对外服务）。
                    //
                    // 代价是启动比较慢。如果只是测某个工具类的方法，
                    // 用普通 JUnit 测试就够了，不必加这个注解。
class Demo5ApplicationTests {

    @Test   // JUnit 5 的注解，标记这是一个测试方法。
            // 运行 mvn test 或点 IDEA 里的绿色三角时，所有带 @Test 的方法都会被执行。
            // 方法要求：无参数、无返回值、非 private。
            // 方法内部没有任何断言，只要执行过程不抛异常就算通过。
    void contextLoads() {
    }

}
