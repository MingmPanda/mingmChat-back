package com.mingm.controller;

import com.mingm.enums.OperatorFriendRequestTypeEnum;
import com.mingm.enums.SearchFriendsStatusEnum;
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

    /**
     * 搜索好友接口, 根据账号做匹配查询而不是模糊查询
     * @param myUserId
     * @param friendUsername
     * @return
     * @throws Exception
     */
    @PostMapping("/search")
    public MingmJSONResult searchUser(String myUserId, String friendUsername)
            throws Exception {
        // 0. 判断 myUserId friendUsername 不能为空
        if (StringUtils.isBlank(myUserId)
                || StringUtils.isBlank(friendUsername)) {
            return MingmJSONResult.errorMsg("");
        }

        // 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);
        if (status.equals(SearchFriendsStatusEnum.SUCCESS.status)) {
            Users user = userService.queryUserInfoByUsername(friendUsername);
            UsersVO userVO = new UsersVO();
            BeanUtils.copyProperties(user, userVO);
            return MingmJSONResult.ok(userVO);
        } else {
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return MingmJSONResult.errorMsg(errorMsg);
        }
    }

    /**
     * 通过搜索好友接口, 根据账号做匹配查询而不是模糊查询
     * @param myUserId
     * @param friendUserId
     * @return
     * @throws Exception
     */
    @PostMapping("/searchQRCode")
    public MingmJSONResult searchUserByQRCode(String myUserId, String friendUserId)
            throws Exception {
        // 0. 判断 myUserId friendUserId 不能为空
        if (StringUtils.isBlank(myUserId)
                || StringUtils.isBlank(friendUserId)) {
            return MingmJSONResult.errorMsg("");
        }

        // 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Integer status = userService.preconditionSearchFriendsByQRCode(myUserId, friendUserId);
        if (status.equals(SearchFriendsStatusEnum.SUCCESS.status)) {
            Users user = userService.queryUserInfoByUserId(friendUserId);
            UsersVO userVO = new UsersVO();
            BeanUtils.copyProperties(user, userVO);
            return MingmJSONResult.ok(userVO);
        } else {
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return MingmJSONResult.errorMsg(errorMsg);
        }
    }

    /**
     * 发送添加好友请求
     * @param myUserId
     * @param friendUsername
     * @return
     * @throws Exception
     */
    @PostMapping("/addFriendRequest")
    public MingmJSONResult addFriendRequest(String myUserId, String friendUsername)
            throws Exception {

        // 0. 判断 myUserId friendUsername 不能为空
        if (StringUtils.isBlank(myUserId)
                || StringUtils.isBlank(friendUsername)) {
            return MingmJSONResult.errorMsg("");
        }

        // 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);
        if (status.equals(SearchFriendsStatusEnum.SUCCESS.status)) {
            userService.sendFriendRequest(myUserId, friendUsername);
        } else {
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return MingmJSONResult.errorMsg(errorMsg);
        }

        return MingmJSONResult.ok();
    }

    /**
     * 发送添加好友的请求
     * @param userId
     * @return
     */
    @PostMapping("/queryFriendRequests")
    public MingmJSONResult queryFriendRequests(String userId) {

        // 0. 判断不能为空
        if (StringUtils.isBlank(userId)) {
            return MingmJSONResult.errorMsg("");
        }

        // 1. 查询用户接受到的朋友申请
        return MingmJSONResult.ok(userService.queryFriendRequestList(userId));
    }

    /**
     * 通过或者忽略朋友请求
     * @param acceptUserId
     * @param sendUserId
     * @param operType
     * @return
     */
    @PostMapping("/operFriendRequest")
    public MingmJSONResult operFriendRequest(String acceptUserId, String sendUserId,
                                             Integer operType) {

        // 0. acceptUserId sendUserId operType 判断不能为空
        if (StringUtils.isBlank(acceptUserId)
                || StringUtils.isBlank(sendUserId)
                || operType == null) {
            return MingmJSONResult.errorMsg("");
        }

        // 1. 如果operType 没有对应的枚举值，则直接抛出空错误信息
        if (StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))) {
            return MingmJSONResult.errorMsg("");
        }

        if (operType.equals(OperatorFriendRequestTypeEnum.IGNORE.type)){
            // 2. 判断如果忽略好友请求，则直接删除好友请求的数据库表记录
            userService.deleteFriendRequest(sendUserId, acceptUserId);
        } else if (operType.equals(OperatorFriendRequestTypeEnum.PASS.type)) {
            // 3. 判断如果是通过好友请求，则互相增加好友记录到数据库对应的表
            //	   然后删除好友请求的数据库表记录
            userService.passFriendRequest(sendUserId, acceptUserId);
        }

        return MingmJSONResult.ok();

    }
}
