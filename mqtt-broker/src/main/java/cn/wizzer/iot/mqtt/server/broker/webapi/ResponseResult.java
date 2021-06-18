package cn.wizzer.iot.mqtt.server.broker.webapi;

import java.io.Serializable;


public class ResponseResult<T> implements Serializable {
    private int code;
    private T data;

    private static String SUCCESS = "SUCCESS";
    private static String ERROR = "ERROR";

    public ResponseResult(){
        super();
    }


    public ResponseResult(int code) {
        this.code = code;
    }

    public ResponseResult(int code, T data){
        this.code = code;
        this.data = data;
    }


    public static<T> ResponseResult<T> genSuccessResult(T data){
        return new ResponseResult(1,data);
    }

    public static<T> ResponseResult<T> genSuccessResult(){
        return new ResponseResult(1);
    }

    public static<T> ResponseResult<T> genErrorResult(){
        return new ResponseResult(-1);
    }

    public static<T> ResponseResult<T> genErrorResult(String msg){
        return new ResponseResult(-1,msg);
    }
}
