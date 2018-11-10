package com.mingm.service.impl;

import com.mingm.mapper.UsersMapper;
import com.mingm.pojo.Users;
import com.mingm.service.UserService;
import com.mingm.utils.FastDFSClient;
import com.mingm.utils.FileUtils;
import com.mingm.utils.QRCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.n3r.idworker.Sid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author: panmm
 * @date: 2018/11/9 15:50
 * @description:
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private UsersMapper usersMapper;

    @Resource
    private Sid sid;

    @Resource
    private QRCodeUtils qrCodeUtils;

    @Resource
    private FastDFSClient fastDFSClient;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Users user = new Users();
        user.setUsername(username);
        Users result = usersMapper.selectOne(user);
        return result != null ? true : false;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String pwd) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();

        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", pwd);

        Users result = usersMapper.selectOneByExample(userExample);


        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users saveUser(Users user) {

        String userId = sid.nextShort();

        // 为每个用户生成一个唯一的二维码
        String qrCodePath = "D:\\mingmChat_img\\qrcode\\user" + userId + "qrcode.png";
        // muxin_qrcode:[username]
        qrCodeUtils.createQRCode(qrCodePath, "mingmChat_qrcode:" + userId);
        MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);

        String qrCodeUrl = "";
        try {
            qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        user.setQrcode(qrCodeUrl);

        user.setId(userId);

        usersMapper.insert(user);
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(Users user) {
        usersMapper.updateByPrimaryKeySelective(user);
        return queryUserById(user.getId());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserById(String userId) {
        return usersMapper.selectByPrimaryKey(userId);
    }
}
