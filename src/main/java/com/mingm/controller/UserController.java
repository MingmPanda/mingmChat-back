package com.mingm.controller;

import com.mingm.pojo.Users;
import com.mingm.pojo.vo.UsersVO;
import com.mingm.service.UserService;
import com.mingm.utils.MingmJSONResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import com.mingm.utils.MD5Utils;

/**
 * @author: panmm
 * @date: 2018/11/9 00:52
 * @description: 用户注册/登录
 */
@RestController
@RequestMapping("u")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册/登录
     * @param user
     * @return
     * @throws Exception
     */
    @PostMapping("/registOrLogin")
    public MingmJSONResult registOrLogin(@RequestBody Users user) throws Exception{
        // 0. 判断用户名和密码不能为空
        if (StringUtils.isBlank(user.getUsername())
                || StringUtils.isBlank(user.getPassword())) {
            return MingmJSONResult.errorMsg("用户名或密码不能为空...");
        }

        // 1. 判断用户名是否存在，如果存在就登录，如果不存在则注册
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
        Users userResult = null;
        if (usernameIsExist) {
            // 1.1 登录
            userResult = userService.queryUserForLogin(user.getUsername(),
                                     MD5Utils.getMD5Str(user.getPassword()));

            if (userResult == null) {
                return MingmJSONResult.errorMsg("用户名或密码不正确...");
            }
        } else {
            // 1.2 注册
            user.setNickname(user.getUsername());
            user.setFaceImage("");
            user.setFaceImageBig("");
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));

            userResult = userService.saveUser(user);
        }
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userResult, usersVO);
        return MingmJSONResult.ok(usersVO);
    }
}
