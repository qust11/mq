package org.example.nameserver.event.listener;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.example.common.coder.TcpMsg;
import org.example.common.enums.NameServerErrorEnum;
import org.example.nameserver.commmon.CommonCache;
import org.example.nameserver.core.PropertiesLoad;
import org.example.nameserver.event.model.RegisterEvent;
import org.example.nameserver.store.ServiceInstance;
import org.example.nameserver.store.ServiceInstanceManager;
import org.example.nameserver.util.BrokerIdentifyUtil;

/**
 * @author qushutao
 * @since 2025/7/16 17:01
 **/
public class RegisterListener implements Listener<RegisterEvent> {


    @Override
    public void onEvent(RegisterEvent event) {
        String password = event.getPassword();
        String username = event.getUsername();
        PropertiesLoad propertiesLoad = CommonCache.getPropertiesLoad();
        String configUsername = propertiesLoad.get("nameserver.username");
        String configPassword = propertiesLoad.get("nameserver.password");
        ChannelHandlerContext ctx = event.getCtx();

        if (!username.equals(configUsername) && !password.equals(configPassword)) {
            TcpMsg tcpMsg = new TcpMsg(NameServerErrorEnum.USER_PASSWORD_ERROR.getCode(), NameServerErrorEnum.USER_PASSWORD_ERROR.getDesc().getBytes());
            ctx.writeAndFlush(tcpMsg);
            ctx.close();
            throw new RuntimeException("用户名或密码错误");
        }

        // 记录链接通道
        Channel channel = ctx.channel();
        channel.attr(AttributeKey.valueOf("reqId")).set(BrokerIdentifyUtil.getBrokerIdentify(event.getBrokerIp(), event.getBrokerPort()));

        ServiceInstanceManager serviceInstanceManager = CommonCache.getServiceInstanceManager();
        ServiceInstance instance = new ServiceInstance();
        instance.setBrokerIp(event.getBrokerIp());
        instance.setBrokerPort(event.getBrokerPort());
        instance.setFirstRegisterTime(System.currentTimeMillis());
        serviceInstanceManager.addServiceInstance(instance);
    }
}
