package com.mingm.push;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: panmm
 * @date: 2018/11/20 10:52
 * @description: 推送
 */
@Slf4j
public class PushtoApp {

    private static String appId = "";
    private static String appKey = "";
    private static String masterSecret = "";
    private static String url = "";

    public static void send (String title, String text, String cid) throws Exception {

        IGtPush push = new IGtPush(url, appKey, masterSecret);

        NotificationTemplate template = notificationTemplateDemo(title, text);

        SingleMessage message = new SingleMessage();
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(24 * 3600 * 3600);
        message.setData(template);
        // 可选 1为WIFI, 0为不限制网络环境。根据手机处于网络情况，决定是否下发
        message.setPushNetWorkType(0);
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(cid);

        IPushResult ret = null;

        try {
            ret = push.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            log.error(e.getMessage(), e);
            ret = push.pushMessageToSingle(message, target, e.getRequestId());
        }


        if (ret != null) {
            log.info(ret.getResponse().toString());
        } else {
            log.info("服务器响应异常");
        }
    }

    public static NotificationTemplate notificationTemplateDemo(String title, String text) {
        NotificationTemplate template = new NotificationTemplate();
        // 设置APPID与APPKEY
        template.setAppId(appId);
        template.setAppkey(appKey);

        // 设置定时展示时间
        // template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");

        Style0 style = new Style0();
        // 设置通知栏标题与内容
        style.setTitle(title);
        style.setText(text);
        // 配置通知栏图标
        style.setLogo("icon.png");
        // 配置通知栏网络图标
        style.setLogoUrl("");
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        template.setStyle(style);

        //透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(1);
        template.setTransmissionContent(text);
        return template;
    }
}
