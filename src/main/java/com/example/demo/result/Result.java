package com.example.demo.result;

public class Result <T>{
    private Integer code;
    private String msg;
    private T data;
    public Result(Integer code,String msg,T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    // ===== 下面三个是"静态工厂方法" =====
    // 好处：调用时不用自己 new，直接 Result.success(x)，读起来像说话
    public static <T>Result<T> success(T data){
        return new Result<>(200,"成功",data);
    }
    public static Result<Void> success(){
        return new Result<>(200,"成功",null);
    }
    public static <T>Result<T> error(Integer code,String msg){
        return new Result<>(code,msg,null);

    }
    // ===== getter 必须有 =====
    // Jackson 是靠 getter 读字段再转 JSON 的，和你 Todo 实体里一样的道理。
    // 没有 getter，返回给前端的 JSON 会是空的 {}，这是个很隐蔽的坑。
    public Integer getCode(){
        return code;
    }
    public String getMsg(){
        return msg;
    }
    public T getData(){
        return data;
    }


}
