package com.aurora.utils;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuroraResult<T> implements Serializable {

    private static final long serialVersionUI = 1L;
    private int states = 0;
    private int code;
    private String message;
    private T data;
    private long currentTimeMillis = System.currentTimeMillis();

    public AuroraResult() {
        this.code = 200;
    }

    public AuroraResult(int code, String message) {
        this.code = code;
        this.states = code;
        this.message = message;
    }

    public AuroraResult(T data) {
        this.code = 200;
        this.data = data;
    }

    public AuroraResult(String message) {
        this.code = 500;
        this.states = 500;
        this.message = message;
    }

    public static <T> AuroraResult<T> fail(String message) {
        return new AuroraResult(message);
    }

    public static <T> AuroraResult<T> fail(CodeMsg codeMsg) {
        return new AuroraResult(codeMsg.getCode(), codeMsg.getMsg());
    }

    public static <T> AuroraResult<T> fail(Integer code, String message) {
        return new AuroraResult(code, message);
    }

    public static <T> AuroraResult<T> success(T data) {
        return new AuroraResult(data);
    }

    public static <T> AuroraResult<T> success(String message) {
        return new AuroraResult(message);
    }
}
