package com.mingm.netty;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author: panmm
 * @date: 2018/11/17 15:03
 * @description: 消息模型
 */
@Getter
@Setter
public class DataContent implements Serializable {

    private static final long serialVersionUID = 8021381444738260454L;

    private Integer action;		// 动作类型
    private ChatMsg chatMsg;	// 用户的聊天内容entity
    private String extand;		// 扩展字段

}
