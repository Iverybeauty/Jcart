package cn.wang.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class BackendUnauthenticationException extends Exception{
    public BackendUnauthenticationException(String message){
        super(message);
    }



}
