package com.example.demo.exception;

import com.example.demo.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 统一处理所有控制器抛出的异常，并返回 JSON
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class) // 捕获资源不存在异常
    @ResponseStatus(HttpStatus.NOT_FOUND) // 设置 HTTP 状态码为 404
    public Result<Void> handleNotFound(NotFoundException e) {
        return Result.error(404, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // 捕获 @Valid 校验失败异常
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 设置 HTTP 状态码为 400
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("参数校验失败");
        return Result.error(400, msg);
    }

    @ExceptionHandler(BusinessException.class) // 捕获违反业务规则的异常
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 设置 HTTP 状态码为 400
    public Result<Void> handleBusiness(BusinessException e) {
        return Result.error(400, e.getMessage());
    }
}
