package cn.wang.interceptor;

import cn.wang.dto.LoginInfo;
import cn.wang.exception.BackendClientException;
import cn.wang.exception.BackendUnauthenticationException;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    /*　Arrays.asList首先，该方法是将数组转化为list。有以下几点需要注意：

　　（1）该方法不适用于基本数据类型（byte,short,int,long,float,double,boolean）

　　（2）该方法将数组与列表链接起来，当更新其中之一时，另一个自动更新

　　（3）不支持add和remove方法*/

    private String[] urls={
                "/user/login",
                "/error",
                "/user/resetPassword",
                "/user/verifyCode"
    };

    /*该方法是判断字符串中是否有子字符串,如果有则返回true，如果没有则返回false。(contains)*/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean contains = Arrays.asList(urls).contains(request.getRequestURI());
        if (contains){
            return true;
        }
        String authorizationStr = request.getHeader("Authorization");
        if (authorizationStr==null){
            throw new BackendUnauthenticationException("Unauthentication");
        }
        String[] s = authorizationStr.split(" ");
        String token = s[1];
        LoginInfo loginInfo;
       try{
           byte[] decode = Base64.getDecoder().decode(token);
           String loginJsonStr = new String(decode, "UTF-8");
           loginInfo = JSON.parseObject(loginJsonStr, LoginInfo.class);

       }catch (Exception ex){
           throw new BackendClientException("auth invalid caused by some issues");
       }
        Long userId = loginInfo.getUserId();
        String username = loginInfo.getUsername();

        long expireTimestamp = loginInfo.getExpirationTime().getTime();
        Date currentTime = new Date();
        long currentTimestamp = currentTime.getTime();
        /*isEmpty是空的意思*/
        if (username==null||username.isEmpty()){
            throw new BackendUnauthenticationException("Unauthentication:username is null or empty");
        }
        if (currentTimestamp>expireTimestamp){
            throw new BackendUnauthenticationException("Unauthentication:token is expired");
        }
        request.setAttribute("userId",userId);
        request.setAttribute("username",username);
        return true;
    }
}
