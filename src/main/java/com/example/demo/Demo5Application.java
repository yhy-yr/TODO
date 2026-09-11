package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动类 —— 整个应用的入口，运行 main 方法就是启动这个服务。
 */
@SpringBootApplication    // 【最核心的注解】它其实是个"组合注解"，等价于同时打了下面三个：
                          //
                          // 1. @SpringBootConfiguration
                          //    标记这个类本身是一份配置（本质上就是个 @Configuration）。
                          //
                          // 2. @EnableAutoConfiguration
                          //    "自动配置"总开关，Spring Boot 最省事的地方就在这。
                          //    它会去看你的 classpath 里有哪些 jar，然后自动配好对应的东西：
                          //      发现 spring-boot-starter-web  → 自动配置内置 Tomcat、端口 8080、Spring MVC
                          //      发现 spring-boot-starter-data-jpa → 自动配置 Hibernate、EntityManager
                          //      发现 mysql-connector-j        → 自动配置数据源（连接池 HikariCP）
                          //    你 application.yml 里写的那些配置，就是用来"覆盖"这些自动配置默认值的。
                          //
                          // 3. @ComponentScan
                          //    自动扫描并注册带 @Component / @RestController / @Service / @Repository
                          //    等注解的类，让它们变成 Spring 管理的 Bean。
                          //    ⚠️ 重点：默认只扫描"启动类所在包及其子包"。
                          //    本类在 com.example.demo 下，所以
                          //      com.example.demo.controller  ✅ 会被扫到
                          //      com.example.demo.entity      ✅ 会被扫到
                          //      com.example.demo.repository  ✅ 会被扫到
                          //      com.other.xxx                ❌ 扫不到，类不会被注册，注入时报错
                          //    这是初学者最常见的"Bean 找不到 / NoSuchBeanDefinitionException"根源：
                          //    新建的包放到了启动类的同级或上级目录里。
                          //    所以启动类必须放在所有业务包的最外层父包上，别随便挪动它的位置。
public class Demo5Application {

    public static void main(String[] args) {
        // 启动整个 Spring 应用：创建 Spring 容器 → 扫描并实例化所有 Bean
        // → 完成自动配置 → 启动内置 Tomcat 开始监听端口。
        // 第一个参数告诉它"以哪个类作为配置入口"，通常就传当前类。
        SpringApplication.run(Demo5Application.class, args);
        // 执行完这行，控制台会打出 "Started Demo5Application in x.xxx seconds"，
        // 看到这行就说明服务起来了，可以访问 http://localhost:8080/todo
    }

}
