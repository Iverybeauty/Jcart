package cn.wang.service;

import cn.wang.dao.UserMapper;
import cn.wang.dto.UserAddDTO;
import cn.wang.dto.UserListDTO;
import cn.wang.dto.UserUpdateDTO;
import cn.wang.po.User;

import cn.wang.util.Constant;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
@EnableAutoConfiguration
public class UserServiceImpl implements UserService {


    @Autowired
    private UserMapper userMapper;


    @Override
    public User getById(Long userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        return user;
    }

    public User getByUsername(String username){
        User user = userMapper.selectByUsername(username);
        return user;
    }

    public void add(UserAddDTO userAddDTO){
        User user = new User();
        user.setUsername(userAddDTO.getUsername());
        user.setFirstName(userAddDTO.getFirstName());
        user.setLastName(userAddDTO.getLastName());
        user.setEmail(userAddDTO.getEmail());
        user.setAvatarUrl(userAddDTO.getAvatarUrl());
        user.setEncryptedPassword(DigestUtils.md5DigestAsHex(userAddDTO.getPassword().getBytes()));
        user.setRoles(Constant.rolesStr);
        userMapper.insert(user);
    }


    public void update(UserUpdateDTO userUpdateDTO){
        User user = userMapper.selectByPrimaryKey(userUpdateDTO.getUserId());
        user.setUsername(userUpdateDTO.getUsername());
        user.setFirstName(userUpdateDTO.getFirstName());
        user.setLastName(userUpdateDTO.getLastName());
        user.setAvatarUrl(userUpdateDTO.getAvatarUrl());
        user.setEmail(userUpdateDTO.getEmail());
        user.setEncryptedPassword(DigestUtils.md5DigestAsHex(userUpdateDTO.getPassword().getBytes()));
        userMapper.updateByPrimaryKey(user);
    }


    public PageInfo<UserListDTO> getUsersWithPage(Integer pageNum){
        //得到total为0（page,rows【一页多少条】）
        PageHelper.startPage(pageNum,3);
        //我要分的是UserListDTO
        Page<UserListDTO> user= userMapper.selectWithPage();
        PageInfo<UserListDTO> pageInfo = user.toPageInfo();
        return pageInfo;

    }

    @Override
    public void batchDelete(List<Integer> userIds) {
        userMapper.batchDelete(userIds);
    }



    public void changeUserPasswordByEmail(String email,String password){
        User user = userMapper.selectByEmail(email);
        //setEncryptedPassword是分配一个密码然后你用MD5加密
        user.setEncryptedPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        userMapper.updateByPrimaryKey(user);
    }




}
