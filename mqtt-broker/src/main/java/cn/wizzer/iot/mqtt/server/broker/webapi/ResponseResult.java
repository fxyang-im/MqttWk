package cn.wizzer.iot.mqtt.server.broker.webapi;

import java.io.Serializable;


public class ResponseResult<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    private static String SUCCESS = "SUCCESS";
    private static String ERROR = "ERROR";

    public ResponseResult(){
        super();
    }

    public ResponseResult(int code, String msg){
        this.code = code;
        this.msg = msg;
    }


    public ResponseResult(int code,String msg, T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


    public static<T> ResponseResult<T> genSuccessResult(T data){
        return new ResponseResult(1,SUCCESS,data);
    }

    public static<T> ResponseResult<T> genSuccessResult(){
        return new ResponseResult(1,SUCCESS);
    }

    public static<T> ResponseResult<T> genErrorResult(){
        return new ResponseResult(-1,ERROR);
    }

    public static<T> ResponseResult<T> genErrorResult(String msg){
        return new ResponseResult(-1,msg);
    }
}
