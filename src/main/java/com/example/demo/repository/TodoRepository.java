package com.example.demo.repository;

import com.example.demo.entity.Todo;
import org.springframework.boot.data.autoconfigure.web.DataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // Spring Data JPA 会根据方法名自动生成查询语句：
    // 相当于 SELECT * FROM todo WHERE done = ?
    Page<Todo> findByDone(Boolean done, Pageable pageable);
    Page<Todo> findByTitleContaining(
            String keyword,
            Pageable pageable
    );

    // 同时根据完成状态和标题关键字查询。
// And 表示两个查询条件必须同时满足。
    Page<Todo> findByDoneAndTitleContaining(
            Boolean done,
            String keyword,
            Pageable pageable
    );

}