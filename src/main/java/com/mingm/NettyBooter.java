package com.mingm;

import com.mingm.netty.WSServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author: panmm
 * @date: 2018/11/9 01:17
 * @description: netty通过SpringBoot启动
 */
@Component
@Slf4j
public class NettyBooter implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(event.getApplicationContext().getParent() == null) {
            try {
                WSServer.getInstance().start();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

    }
}
