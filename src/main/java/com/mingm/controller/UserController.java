package com.mingm.controller;

import com.mingm.pojo.Users;
import com.mingm.pojo.bo.UsersBO;
import com.mingm.pojo.vo.UsersVO;
import com.mingm.service.UserService;
import com.mingm.utils.FastDFSClient;
import com.mingm.utils.FileUtils;
import com.mingm.utils.MingmJSONResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import com.mingm.utils.MD5Utils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Random;

/**
 * @author: panmm
 * @date: 2018/11/9 00:52
 * @description: 用户注册/登录
 */
@RestController
@RequestMapping("u")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private FastDFSClient fastDFSClient;

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

    /**
     * 上传用户头像
     * @param userBO
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadFaceBase64")
    public MingmJSONResult uploadFaceBase64(@RequestBody UsersBO userBO) throws Exception {
        log.info("上传用户头像开始");
        // 获取前端传过来的base64字符串, 然后转换为文件对象再上传
        String base64Data = userBO.getFaceData();
        String userFacePath = "D:\\mingmChat_img\\userFace\\"
                + userBO.getUserId()
                +new Random().nextInt(
                100000000)
                + "userface64.png";
        FileUtils.base64ToFile(userFacePath, base64Data);

        // 上传文件到fastdfs
        log.info("上传文件到fastdfs开始");
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
        log.info("上传文件到fastdfs结束");
        String url = fastDFSClient.uploadBase64(faceFile);
        log.info("存储头像的url = " + url);

        //		"dhawuidhwaiuh3u89u98432.png"
        //		"dhawuidhwaiuh3u89u98432_80x80.png"

        // 获取缩略图的url
        String thump = "_80x80.";
        String arr[] = url.split("\\.");
        String thumpImgUrl = arr[0] + thump + arr[1];

        // 更新用户头像
        Users user = new Users();
        user.setId(userBO.getUserId());
        user.setFaceImage(thumpImgUrl);
        user.setFaceImageBig(url);

        Users result = userService.updateUserInfo(user);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(result, usersVO);
        log.info("上传用户头像结束");
        return MingmJSONResult.ok(usersVO);
    }

    /**
     * 设置昵称
     * @param userBO
     * @return
     * @throws Exception
     */
    @PostMapping("/setNickname")
    public MingmJSONResult setNickname(@RequestBody UsersBO userBO) throws Exception {

        Users user = new Users();
        user.setId(userBO.getUserId());
        user.setNickname(userBO.getNickname());

        Users result = userService.updateUserInfo(user);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(result, usersVO);
        return MingmJSONResult.ok(usersVO);
    }

    /**
     * 修改帐号
     * @param userBO
     * @return
     * @throws Exception
     */
    @PostMapping("/setUsername")
    public MingmJSONResult setUsername(@RequestBody UsersBO userBO) throws Exception {
        if (userService.queryUsernameIsExist(userBO.getUsername())) {
            return MingmJSONResult.errorMsg("该帐号名重复或已被占用！");
        }

        Users user = new Users();
        user.setId(userBO.getUserId());
        user.setUsername(userBO.getUsername());

        Users result = userService.updateUserInfo(user);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(result, usersVO);
        return MingmJSONResult.ok(usersVO);
    }
}
