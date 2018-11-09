package com.mingm.service;

import com.mingm.pojo.Users;

/**
 * @author: panmm
 * @date: 2018/11/9 15:47
 * @description:
 */
public interface UserService {

    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    boolean queryUsernameIsExist(String username);

    /**
     * 查询用户是否存在
     * @param username
     * @param pwd
     * @return
     */
    Users queryUserForLogin(String username, String pwd);

    /**
     * 用户注册
     * @param user
     * @return
     */
    Users saveUser(Users user);
}
