package com.example.camel.component.dingpass;

/**
 * DingPass组件的业务异常
 */
public class DingPassException extends RuntimeException {
    public DingPassException(String msg){
        super(msg);
    }

    public DingPassException(String msg, Throwable e){
        super(msg,e);
    }
}
