package com.mingm.service;

import com.mingm.pojo.Users;
import com.mingm.pojo.vo.FriendRequestVO;
import com.mingm.pojo.vo.MyFriendsVO;

import java.util.List;

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

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    Users updateUserInfo(Users user);

    /**
     * 搜索朋友的前置条件
     * @param myUserId
     * @param friendUsername
     * @return
     */
    Integer preconditionSearchFriends(String myUserId, String friendUsername);

    /**
     * 根据用户名查询用户对象
     * @param username
     * @return
     */
    Users queryUserInfoByUsername(String username);

    /**
     * 搜索朋友的前置条件
     * @param myUserId
     * @param friendUserId
     * @return
     */
    Integer preconditionSearchFriendsByQRCode(String myUserId, String friendUserId);

    /**
     * 根据用户ID查询用户对象
     * @param userId
     * @return
     */
    Users queryUserInfoByUserId(String userId);

    /**
     * 添加好友请求记录，保存到数据库
     * @param myUserId
     * @param friendUsername
     */
    void sendFriendRequest(String myUserId, String friendUsername);

    /**
     * 查询好友请求
     * @param acceptUserId
     * @return
     */
    List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    /**
     * 删除好友请求记录
     * @param sendUserId
     * @param acceptUserId
     */
    void deleteFriendRequest(String sendUserId, String acceptUserId);

    /**
     * 通过好友请求
     * 1. 保存好友
     * 2. 逆向保存好友
     * 3. 删除好友请求记录
     * @param sendUserId
     * @param acceptUserId
     */
    void passFriendRequest(String sendUserId, String acceptUserId);

    /**
     * 查询好友列表
     * @param userId
     * @return
     */
    List<MyFriendsVO> queryMyFriends(String userId);

}
