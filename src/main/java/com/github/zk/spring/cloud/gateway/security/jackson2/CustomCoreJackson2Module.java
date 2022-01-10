package com.github.zk.spring.cloud.gateway.security.jackson2;

import com.github.zk.spring.cloud.gateway.security.authentication.WeChatAuthenticationToken;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatUserInfo;
import org.springframework.security.jackson2.CoreJackson2Module;

/**
 * 自定义 Jackson 模型
 *
 * @author zk
 * @date 2021/12/7 14:51
 */
public class CustomCoreJackson2Module extends CoreJackson2Module {

    @Override
    public void setupModule(SetupContext context) {
        //设置支持微信token序列化
        context.setMixInAnnotations(WeChatAuthenticationToken.class, WeChatAuthenticationTokenMixin.class);
        // 设置支持微信用户信息序列化
        context.setMixInAnnotations(WeChatUserInfo.class, WeChatUserMixin.class);
        super.setupModule(context);
    }
}
