package cn.wang.dao;

import cn.wang.dto.UserListDTO;
import cn.wang.po.User;
import com.github.pagehelper.Page;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByUsername(String username);

    Page<UserListDTO> selectWithPage();

    User selectByEmail(String email);

    void batchDelete(List<Integer> userIds);

}