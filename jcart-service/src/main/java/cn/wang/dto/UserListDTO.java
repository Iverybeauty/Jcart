package cn.wang.dto;

import java.util.Date;

public class UserListDTO {

    private Long userId;
    private String username;
//    private Integer state;
//    private Date createTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
