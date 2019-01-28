package cn.wang.dto;

public class UserUpdateDTO extends UserAddDTO{

    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
