package org.example.nameserver.handler;


import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.common.coder.TcpMsg;
import org.example.nameserver.event.EventBus;
import org.example.nameserver.event.model.Event;

/**
 * @author qushutao
 * @since 2025/7/15 9:27
 **/
@ChannelHandler.Sharable
public class TcpServerHandler extends SimpleChannelInboundHandler {

    private EventBus eventBus;

    public TcpServerHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        TcpMsg tcpMsg = JSON.parseObject(o.toString(), TcpMsg.class);

        int code = tcpMsg.getCode();
        eventBus.publish(new Event());
    }
}
