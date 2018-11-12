package com.mingm.mapper;

import com.mingm.pojo.Users;
import com.mingm.pojo.vo.FriendRequestVO;
import com.mingm.pojo.vo.MyFriendsVO;
import com.mingm.utils.MyMapper;

import java.util.List;

public interface UsersMapperCustom extends MyMapper<Users> {
	
	List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

	List<MyFriendsVO> queryMyFriends(String userId);

	
}