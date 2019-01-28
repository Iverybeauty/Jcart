package cn.wang.service;

import cn.wang.dto.UserAddDTO;
import cn.wang.dto.UserListDTO;
import cn.wang.dto.UserUpdateDTO;
import cn.wang.po.User;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface UserService {

    User getById(Long userId);

    void add(UserAddDTO userAddDTO);

   User getByUsername(String username);

   PageInfo<UserListDTO> getUsersWithPage(Integer pageNum);

    void batchDelete(List<Integer> userIds);

    void update(UserUpdateDTO userUpdateDTO);

    void changeUserPasswordByEmail(String email,String password);




}
