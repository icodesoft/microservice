package com.icodesoft.auth.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResponseModel<T> {

    private int code;

    private String message;

    private T data;

    public static <T> ResponseModel<T> loginSuccess(T data) {
        return new ResponseModel<T>(200, "authentication success", data);
    }

    public static <T> ResponseModel<T> loginFailure(T data) {
        return new ResponseModel<T>(401, "authentication failure", data);
    }
}
