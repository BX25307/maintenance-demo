package xyz.bx25.demo.common;


import lombok.Data;

@Data
public class Response<T> {
    private Integer code; // 200 成功, 500 失败
    private String msg;
    private T data;

    // 成功静态方法
    public static <T> Response<T> success(T data) {
        Response<T> r = new Response<>();
        r.setCode(200);
        r.setMsg("success");
        r.setData(data);
        return r;
    }

    public static <T> Response<T> success() {
        return success(null);
    }

    // 失败静态方法
    public static <T> Response<T> error(String msg) {
        Response<T> r = new Response<>();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }
}