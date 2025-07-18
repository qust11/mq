package org.example.nameserver.event.listener;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.example.common.coder.TcpMsg;
import org.example.common.enums.NameServerErrorEnum;
import org.example.nameserver.commmon.CommonCache;
import org.example.nameserver.event.model.Event;
import org.example.nameserver.event.model.RegisterEvent;
import org.example.nameserver.event.model.UnregisterEvent;
import org.example.nameserver.store.ServiceInstanceManager;
import org.example.nameserver.util.BrokerIdentifyUtil;

/**
 * @author qushutao
 * @since 2025/7/16 17:01
 **/
public class UnregisterListener implements Listener<UnregisterEvent>{
    @Override
    public void onEvent(UnregisterEvent event) {
        ChannelHandlerContext ctx = event.getCtx();
        Channel channel = ctx.channel();
        Object reqId = channel.attr(AttributeKey.valueOf("reqId")).get();
        if (null == reqId){
            TcpMsg tcpMsg = new TcpMsg(NameServerErrorEnum.REQ_ID_MISS_ERROR.getCode(), NameServerErrorEnum.REQ_ID_MISS_ERROR.getDesc().getBytes());
            ctx.writeAndFlush(tcpMsg);
            ctx.close();
            throw new RuntimeException("请求ID丢失");
        }

        ServiceInstanceManager serviceInstanceManager = CommonCache.getServiceInstanceManager();
        serviceInstanceManager.removerInstance((String) reqId);
    }
}
