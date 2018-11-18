package com.mingm.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: panmm
 * @date: 2018/11/8 00:27
 * @description: 用于检测channel的心跳handler
 * 继承ChannelInboundHandlerAdapter，从而不需要实现channelRead0方法
 */
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        // 判断evt是否是IdleStateEvent（用于触发用户事件，包含 读空闲/写空闲/读写空闲 ）
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            if (event.state().equals(IdleState.READER_IDLE)) {
                log.info("进入读空闲...");
            } else if (event.state().equals(IdleState.WRITER_IDLE)){
                log.info("进入写空闲...");
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                log.info("channel关闭前，users的数量为：" + ChatHandler.users.size());

                Channel channel = ctx.channel();
                // 关闭无用的channel，以防资源浪费
                channel.close();

                log.info("channel关闭后，users的数量为：" + ChatHandler.users.size());
            }
        }
    }
}
