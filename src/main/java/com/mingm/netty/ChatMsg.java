package com.mingm.netty;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author: panmm
 * @date: 2018/11/17 15:08
 * @description: 聊天模型
 */
@Getter
@Setter
public class ChatMsg implements Serializable {

    private static final long serialVersionUID = 3611169682695799175L;

    private String senderId;		// 发送者的用户id
    private String receiverId;		// 接受者的用户id
    private String msg;				// 聊天内容
    private String msgId;			// 用于消息的签收

}
