package cn.wang.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BackendClientException extends Exception{
    public BackendClientException(String message){
        super(message);
    }


}
