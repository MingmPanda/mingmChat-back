package com.mingm.push;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author: panmm
 * @date: 2018/11/20 11:04
 * @description: 异步执行推送
 */
@Component
@Slf4j
public class AsynCenter {

    private static class InnerClass {
        private static AsynCenter asynCenter =
                new AsynCenter();
    }
    public static AsynCenter getInstance() {
        return InnerClass.asynCenter;
    }

    private AsynCenter(){
    }

    @Async
    public void sendPush(String title, String text, String cid) {
        try {
            PushtoApp.send(title, text, cid);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
